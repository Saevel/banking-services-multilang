from flask_restful import Resource
from flask import Response

import Account


class AccountsController(Resource):

    def __init__(self, repository):
        self._repository = repository

    def delete_account_by_id(self, id):
        self._repository.delete_account_by_id(id)
        return Response('', 204)

    def get_all_accounts(self):
        accounts = self._repository.find_all_accounts()
        results = [
            {"id": account.id, "userId": account.user_id, "currency": account.currency, "balance": account.balance}
            for account in accounts
        ]

        if not results:
            return {"accounts": []}
        else:
            return {"accounts": results}

    def create_account(self, request):
        # TODO: Content validation !
        # TODO: Initial balance

        self._repository.insert_account(Account.Account(
            0,
            request.json["userId"],
            request.json["currency"],
            request.json["balance"]
        ))

        return Response('', 204)