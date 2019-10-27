#!/usr/bin/python3
"""
Starts a Flask web application
"""


from flask import Flask, render_template
from flask_mongoalchemy import MongoAlchemy


app = Flask(__name__)
app.config["MONGOALCHEMY_DATABASE"] = "cats"
db = MongoAlchemy(app)
app.config['ENV'] = 'development'
app.config['DEBUG'] = True
app.config['TESTING'] = True


class Cats(db.Document):
    name = db.StringField()
    phone = db.StringField()


def create():
    cat = Cats(name="Test", phone="Default")
    cat.save()

def update(name):
    cat = Cats.query.filter(Cats.name == name).first()
    cat.save()

def remove(id):
    cat = Cats.query.get(id)
    cat.remove()

@app.route('/', strict_slashes=False)
def index():
    return render_template('index.html')


@app.route('/about', strict_slashes=False)
def about():
    return render_template('about.html')


@app.route('/download', strict_slashes=False)
def download():
    return render_template('download.html')


@app.route('/phone/save', strict_slashes=False)
def phonesave():
    # exists route linked
    # 200 connect, 201 create register, 204 no content,
    return '{"status":"200"}'

#@app.route('phone/report')
#def phonereport():
#    pass


if __name__ == '__main__':
        app.run(host='0.0.0.0', port='5000')

#curl -X POST -H "Content-Type: application/json" -d @phone/cats.json http://localhost:8080/phonesave
