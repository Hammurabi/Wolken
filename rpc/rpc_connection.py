# import requests to make our lives easier
import requests

class rpc_connection:
    def __init__(self):
        self.ip      = 'localhost'
        self.port    = '12560'
        self.token   = ''
    def sendRequest(request, arguments):
        pass