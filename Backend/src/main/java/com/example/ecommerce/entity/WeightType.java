package com.example.ecommerce.entity;

public enum WeightType {
        KILO_1("1KG"),
        KILO_2("2KG"),
        KILO_3("3KG"), // Ajout de la valeur "3KG"
        KILO_5("5KG");

        private final String label;

        WeightType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
}



