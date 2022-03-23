
class Account:

    def __init__(self, account_id, user_id, currency, balance):
        self._account_id = account_id
        self._user_id = user_id
        self._currency = currency
        self._balance = balance

    @property
    def account_id(self):
        return self._account_id

    @account_id.setter
    def id(self, account_id):
        self._account_id = account_id

    @property
    def user_id(self):
        return self._user_id

    @user_id.setter
    def user_id(self, user_id):
        self._user_id = user_id

    @property
    def currency(self):
        return self._currency

    @currency.setter
    def currency(self, currency):
        self._currency = currency

    @property
    def balance(self):
        return self._balance

    @balance.setter
    def balance(self, balance):
        self._balance = balance
