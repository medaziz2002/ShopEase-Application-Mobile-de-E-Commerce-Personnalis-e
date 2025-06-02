from sentence_transformers import SentenceTransformer, util
import flask
import string
from flask import request, jsonify
import mysql.connector
from nltk import sent_tokenize, word_tokenize
from nltk.stem import PorterStemmer, SnowballStemmer
from nltk.stem import WordNetLemmatizer
from sklearn.feature_extraction.text import CountVectorizer
from nltk import pos_tag
from gensim.models import Word2Vec

import operator
import nltk
try:
    nltk.data.find('tokenizers/punkt')
    nltk.data.find('corpora/wordnet')
except LookupError:
    nltk.download('punkt')
    nltk.download('wordnet')
    nltk.download('stopwords')

nltk.download('punkt_tab')
nltk.download('omw-1.4')


def preprocess_text(text):
    """Preprocesses text by lowercasing, removing punctuation, and performing stemming/lemmatization."""
    # Lowercase the text
    text = text.lower()

    # Remove punctuation
    text = ''.join([char for char in text if char not in string.punctuation])

    # Steeming (optional)
    ps = PorterStemmer()
    stemmed_sentence = " ".join(ps.stem(word) for word in word_tokenize(text))

    # Lemmatisation
    lemmatizer = WordNetLemmatizer()
    lemmatized_sentence = " ".join(lemmatizer.lemmatize(word.lower()) for word in word_tokenize(text))

    return lemmatized_sentence

app = flask.Flask(__name__)
app.config.from_object('config.Config')
app.config["DEBUG"] = True

def connect_to_database():
    try:
        mydb = mysql.connector.connect(
            host="localhost",
            user="root",
            password="",
            database="e-commerce"
        )
        return mydb
    except mysql.connector.Error as err:
        print("Error connecting to the database:", err)
        return None

# Load the BERT model
model = SentenceTransformer('all-mpnet-base-v2')

def get_all_reclamation_descriptions():
    mydb = connect_to_database()
    if mydb is not None:
        mycursor = mydb.cursor()

        query = "SELECT faq_id, question, reponse FROM faq"
        mycursor.execute(query)
        results = mycursor.fetchall()

        mycursor.close()
        mydb.close()

        return results  # Return list of tuples (id, question, response)
    else:
        return []

def find_best_match_for_question(input_question, faqs):
    """Find the best matching FAQ for a single question"""
    if not faqs:
        return {
            "id": None,
            "question": input_question,
            "reponse": "Aucune réponse trouvée pour cette question. Veuillez contacter l'agent support.",
            "similarity": None
        }

    # Separate the IDs, questions, and responses
    ids = [faq[0] for faq in faqs]
    questions = [faq[1] for faq in faqs]
    responses = [faq[2] for faq in faqs]

    sentences = [input_question] + questions

    # Preprocess sentences using the separate function
    preprocessed_sentences = [preprocess_text(sentence) for sentence in sentences]

    # Encode preprocessed sentences
    embeddings = model.encode(preprocessed_sentences, convert_to_tensor=True)

    # Calculate cosine similarities
    cosine_scores = util.cos_sim(embeddings, embeddings)

    similarity_data = []

    for i in range(1, len(sentences)):
        similarity = cosine_scores[0][i].item() * 100
        similarity_data.append({
            "id": ids[i-1],  # Subtract 1 because sentences[0] is the input_question
            "question": questions[i-1],
            "reponse": responses[i-1],
            "similarity": similarity
        })

        print(f"Similarity semantic between '{sentences[0]}' and '{sentences[i]}' is {similarity:.2f}%")

    # Sort similarity_data by similarity in descending order
    similarity_data.sort(key=operator.itemgetter('similarity'), reverse=True)

    # Return the response with the highest similarity
    if similarity_data and similarity_data[0]['similarity'] >= 50:
        return similarity_data[0]
    else:
        return {
            "id": None,
            "question": input_question,
            "reponse": "Aucune réponse trouvée pour cette question. Veuillez contacter l'agent support.",
            "similarity": None
        }

# Route pour une seule question (garde la compatibilité avec l'ancien code)
@app.route('/similarityQuestion', methods=['POST'])
def similarity():
    request_data = request.json
    
    if not request_data:
        return jsonify({"error": "No data provided"}), 400
    
    # Accepte soit "sentence" soit "sentences"
    if "sentence" in request_data:
        input_sentence = request_data["sentence"]
        if not input_sentence:
            return jsonify({"error": "No sentence provided"}), 400
        
        faqs = get_all_reclamation_descriptions()
        result = find_best_match_for_question(input_sentence, faqs)
        return jsonify(result)
    
    elif "sentences" in request_data:
        # Rediriger vers la fonction de traitement multiple
        return similarity_multiple()
    
    else:
        return jsonify({"error": "No sentence or sentences provided"}), 400

# Nouvelle route pour plusieurs questions
@app.route('/similarityQuestions', methods=['POST'])
def similarity_multiple():
    # Accepte soit "sentences" (liste) soit "sentence" (string unique)
    request_data = request.json
    
    if not request_data:
        return jsonify({"error": "No data provided"}), 400
    
    input_sentences = []
    
    # Vérifier si c'est une liste de phrases ou une phrase unique
    if "sentences" in request_data:
        input_sentences = request_data["sentences"]
        if not isinstance(input_sentences, list):
            return jsonify({"error": "sentences must be a list"}), 400
    elif "sentence" in request_data:
        input_sentences = [request_data["sentence"]]
    else:
        return jsonify({"error": "No sentences or sentence provided"}), 400
    
    if not input_sentences:
        return jsonify({"error": "Empty sentences list provided"}), 400

    # Récupérer toutes les FAQs une seule fois
    faqs = get_all_reclamation_descriptions()
    
    if not faqs:
        return jsonify({"error": "No data found in the database"}), 500

    # Traiter chaque question
    results = []
    for i, sentence in enumerate(input_sentences):
        if not sentence or not sentence.strip():
            results.append({
                "question_index": i,
                "original_question": sentence,
                "result": {
                    "id": None,
                    "question": sentence,
                    "reponse": "Question vide fournie.",
                    "similarity": None
                }
            })
            continue
            
        result = find_best_match_for_question(sentence, faqs)
        results.append({
            "question_index": i,
            "original_question": sentence,
            "result": result
        })

    return jsonify({
        "total_questions": len(input_sentences),
        "results": results
    })

# *** AJOUT DE LA ROUTE /allQuestions ***
@app.route('/allQuestions', methods=['GET'])
def all_questions():
    faqs = get_all_reclamation_descriptions()
    questions = [faq[1] for faq in faqs]  # faq[1] correspond à la question
    return jsonify(questions)

if __name__ == '__main__':
    port = app.config.get('PORT', 8080)
    app.run(port=port)
