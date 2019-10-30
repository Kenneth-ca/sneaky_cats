from pymongo import MongoClient
from datetime import datetime
import json
"""
Module to create and use database with mongodb
"""


# establish connection
conn = MongoClient('localhost', 27017)
conn = MongoClient()

# create db
db = conn.CatsDB

# create collection
collection = db.CatsCollect


def InsertDataCats(CatData_date):
    """
    This function adds a date for the register
    """
    insertCat = collection.insert(CatData_date)


def UpdateCountCats(count_int):
    """
    This function updates a register
    """
    myquery = ({"count_visitors": count_int})
    count_int = 1 + count_int
    print(count_int)
    newvalues = { "$set": { "count_visitors": count_int } }
    collection.update(myquery, newvalues)