from flask import Flask
from flask import jsonify
from flask import request
from flask_pymongo import PyMongo
from nltk.stem import PorterStemmer
import re
#import pymongo

app = Flask(__name__)
app.config['MONGO_DBNAME'] = 'prettyprinted_rest'
app.config['MONGO_URI'] = 'mongodb+srv://admin:Khaled1999@cluster0.5toq8.mongodb.net/myDatabase?retryWrites=true&w=majority'

mongo = PyMongo(app)



@app.route('/<word>', methods=['GET'])
def query_word(word):
    indexer = mongo.db.indexer

    processedword = processing(word)
    q = indexer.find_one({'word': processedword})
    if(q):
        data = {'data': q['data']}
        return data, 200
    else:
        data = "No results found"
        return data, 404
    # indexer = mongo.db.indexer

    # processedword = processing(word)
    # results = indexer.find( { "word": { "$regex": processedword , "$options" :'im' } } )
    
    # output = []

    # if(results):
    #     for j in results:
    #         output.append({'data': j['data']})
    #     return jsonify({"results": output}), 200
    # else:
    #     output = "No results found"
    #     return output, 404



@app.route('/suggest/<word>', methods=['GET'])
def suggest_word(word):
    indexer = mongo.db.indexer

    processedword = processing(word)
    data = indexer.find(  { "word": { "$regex": processedword , "$options" :'im' } } )

    output = []

    if(data):
        for j in data:
            output.append({'word': j['word']})
        return jsonify({'results': output}), 200


def processing(s):
    ps = PorterStemmer()
    s = ps.stem(s)
    s=s.lower()
    s=s.strip()
    s=s.replace(" ","")
    s=s.replace("-","")
    s=s.replace("’","")
    s=s.replace("@","")
    s=s.replace("#","")
    s=s.replace("_","")
    s=s.replace("%","")
    s=s.replace("*","")
    s=s.replace(";","")
    s=s.replace("(","")
    s=s.replace(")","")
    s=s.replace("{","")
    s=s.replace("}","")
    s=s.replace("]","")
    s=s.replace("[","")
    s=s.replace("\n","")
    s=s.replace("/","")
    s=s.replace("?","")
    s=s.replace(">","")
    s=s.replace("<","")
    s=s.replace("!","")
    s=s.replace(",","")
    return s


if __name__ == '__main__':
    app.run(debug=True)
