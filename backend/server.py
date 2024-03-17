# app.py (Flask Application)
from flask import Flask, request, jsonify
import model

app = Flask(__name__)

def predict(data):
    # Placeholder for your predict function
    # Process the data
    result = {"prediction": "Example result based on " + str(data)}
    return result

@app.route('/predict', methods=['POST'])
def predict_route():
    if request.method == 'POST':
        data = request.get_json()
        result = model.predict_forcaast(data)
        return jsonify(result)

@app.route('/',methods=['GET'])
def index():
    return "Hello World!"

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0')
