from flask import Flask, request
from flask_restful import Resource, Api

from sqlalchemy import create_engine

import AccountsRepository
import AccountsController


class HelloWorld(Resource):
    def get(self):
        return "Hello, Python!"


engine = create_engine("sqlite:///myexample.db", echo=True, future=True)

repository = AccountsRepository.AccountsRepository(engine)
repository.create_table()

controller = AccountsController.AccountsController(repository)

app = Flask(__name__)
api = Api(app)


@app.route("/accounts/<int:id>", methods=["DELETE"])
def delete_account_by_id(id):
    return controller.delete_account_by_id(id)


@app.route("/accounts", methods=["GET"])
def get_all_accounts():
    return controller.get_all_accounts()


@app.route("/accounts", methods=["POST"])
def create_account():
    return controller.create_account(request)


api.add_resource(HelloWorld, '/')

if __name__ == '__main__':
    app.run(host="0.0.0.0", debug=True)