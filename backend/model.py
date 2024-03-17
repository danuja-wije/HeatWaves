import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import LatentDirichletAllocation
from sklearn.feature_extraction.text import CountVectorizer
import numpy as np
import nltk
from nltk.stem import WordNetLemmatizer
from nltk.corpus import stopwords
import re

def predict_forcaast(data):
    file_path = 'survey_combined -final.csv'
    df = pd.read_csv(file_path)
    df = df.dropna(how='all')
    numerical_fields = ['Current Weight (kg)','Typical Workday in Heatwave', 'Determine Water Intake', 'Medication Effect on Hydration', 'Feel When Dehydrated', 'Severe Dehydration Experience', 'Hydration Habits', 'Tools for Monitoring Hydration']
    categorical_fields = ['Gender', 'Diuretics Medication', 'Beta-Blockers Medication', 'Medical Records Consent']
    text_columns = [
        'Typical Workday in Heatwave',
        'Determine Water Intake',
        'Medication Effect on Hydration',
        'Feel When Dehydrated',
        'Severe Dehydration Experience',
        'Hydration Habits',
        'Tools for Monitoring Hydration'
    ]
    # Extract numerical and categorical data
    numerical_data = df[numerical_fields]
    categorical_data = df[categorical_fields]

    # One-hot encode categorical data
    categorical_data_encoded = pd.get_dummies(categorical_data)

    # Combine numerical and encoded categorical data
    X = pd.concat([numerical_data, categorical_data_encoded], axis=1)
    # Separate numerical and categorical fields
    numerical_fields = ['Current Weight (kg)']
    categorical_fields = ['Gender', 'Diuretics Medication', 'Beta-Blockers Medication', 'Medical Records Consent']
    text_columns = [
        'Typical Workday in Heatwave',
        'Determine Water Intake',
        'Medication Effect on Hydration',
        'Feel When Dehydrated',
        'Severe Dehydration Experience',
        'Hydration Habits',
        'Tools for Monitoring Hydration'
    ]

    # Function to preprocess text data
    def preprocess_text(text):
        # Lowercasing
        text = str(text).lower()
        # Remove non-alphabetic characters
        text = re.sub(r'[^a-zA-Z\s]', '', text)
        # Tokenization (implicitly done by CountVectorizer later)
        # Remove stopwords
        stop_words = set(stopwords.words('english'))
        text = ' '.join([word for word in text.split() if word not in stop_words])
        # Lemmatization
        lemmatizer = WordNetLemmatizer()
        text = ' '.join([lemmatizer.lemmatize(word) for word in text.split()])
        return text

    # Enhanced extract_topics function with preprocessing
    def extract_topics(data, n_topics=3, n_features=1000):
        # Preprocess the text data
        preprocessed_data = data.apply(preprocess_text)

        # Vectorization with TF-IDF to give less weight to more common words
        vectorizer = TfidfVectorizer(max_features=n_features, stop_words='english')
        dtm = vectorizer.fit_transform(preprocessed_data)

        # Apply LDA
        lda = LatentDirichletAllocation(n_components=n_topics, random_state=42)
        lda.fit(dtm)

        # Get the topic distribution for each document
        topic_distribution = lda.transform(dtm)
        return topic_distribution

    for column in text_columns:
        topics = extract_topics(df[column])
        for i in range(topics.shape[1]):
            X[f'{column}_Topic_{i}'] = topics[:, i]

    X= X.drop(columns=text_columns)

    # Standardize the data
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    # Apply K-Means clustering to the scaled features
    kmeans_combined = KMeans(n_clusters=3, random_state=42)
    kmeans_combined.fit(X_scaled)  # Use X_scaled directly

    # Assign the clusters as the predicted body water levels with combined features
    df['Predicted Body Water Level Combined'] = kmeans_combined.labels_

    # Check the distribution of predicted body water levels with combined features
    # print(df['Predicted Body Water Level Combined'].value_counts())

    forcast_df = pd.read_csv('extended_heatwave_predictions_2024.csv')

    # Define the recommendation function
    def recommend_work_time(alert_level, body_water_level):

        if alert_level in ["Low", "Moderate"]:
            if body_water_level == 1:  # Low body water level
                return "Avoid peak heat; Stay hydrated"
            else:  # Medium or High body water level
                return "Suitable with caution; Stay hydrated"
        elif alert_level in ["High", "Extreme"]:
            return "Not suitable; Avoid strenuous activities"
        else:
            return "Check local advisories"

    # Apply the recommendation function to the heatwave dataset
    # For demonstration, using a placeholder for medium body water level (0) for all entries
    forcast_df['General Recommendation'] = forcast_df['Alert Level'].apply(
        lambda x: recommend_work_time(x, 0)  # Using 0 as a placeholder for a medium body water level
    )
    forcast_df.columns = ['Date','Temperature','Humidity','Heat Index', 'Alert Level', 'General Recommendation']
    forcast_df.to_json(orient='records').to_text('data.json')
    return str(forcast_df.to_json(orient='records'))

# print(predict_forcaast('data'))