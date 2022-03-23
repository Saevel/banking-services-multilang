from sqlalchemy import text

import Account


class AccountsRepository:

    def __init__(self, engine):
        self._engine = engine

    def create_table(self):
        with self._engine.connect() as conn:
            conn.execute(text(
                """CREATE TABLE IF NOT EXISTS Accounts(
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                USER_ID INTEGER,
                CURRENCY CHAR(3),
                BALANCE DOUBLE
                )"""
            ))

            conn.commit()

    def insert_account(self, account):
        with self._engine.connect() as conn:
            conn.execute(
                text("INSERT INTO ACCOUNTS(USER_ID, CURRENCY, BALANCE) VALUES(:user_id, :currency, :balance)"),
                [{"user_id": account.user_id, "currency": account.currency, "balance": account.balance}]
            )


            conn.commit()

    def find_all_accounts(self):
        accounts = []
        with self._engine.connect() as conn:
            result = conn.execute(text("SELECT id, user_id, currency, balance FROM Accounts"))

            for row in result:
                accounts.append(Account.Account(row[0], row[1], row[2], row[3]))

        return accounts

    def delete_account_by_id(self, id):

        with self._engine.connect() as conn:
            conn.execute(
                text("DELETE FROM Accounts WHERE id = :id"),
                [{"id": id}]
            )


            conn.commit()