#!/usr/bin/python3
"""
Starts a Flask web application
"""
from flask import Flask, render_template, request
from datetime import datetime
import mongofile


app = Flask(__name__)
app.config['ENV'] = 'development'
app.config['DEBUG'] = True
app.config['TESTING'] = True


@app.route('/', strict_slashes=False)
def index():
    """ Index route """
    return render_template('index.html')


@app.route('/about', strict_slashes=False)
def about():
    """ About route for more information """
    return render_template('about.html')


@app.route('/download', strict_slashes=False)
def download():
    """ Route to download app """
    return render_template('download.html')


@app.route('/phone/json', methods=['POST'])
def postJsonHandler():
    """ Route to recieve json and insert into database """
    CatData = request.get_json()
    CatData.update({"FECHA": datetime.now().strftime("%m/%d/%Y, %H:%M:%S")})
    mongofile.InsertDataCats(CatData)
    return '{"status":"200"}'


@app.route('/phone/report', strict_slashes=False)
def report():
    """ Route to present report """
    print("Hola")
    data = mongofile.collection.find()
    list = []
    for c in data:
        list.append(c)
    return render_template('report.html', list=list)


if __name__ == '__main__':
        app.run(host='0.0.0.0', port='5000')
