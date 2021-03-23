# import requests to make our lives easier
import requests
import json
from types import SimpleNamespace

# define a 'connection' class to hold all the connection information
class rpc_connection:
    def __init__(self):
        self.ip      = 'localhost'
        self.port    = '12560'
        self.token   = ''
    def sendRequest(request, arguments):
        url = self.ip + ":" + self.port + "/" + packageQuery(requests, arguments)
        return toJson(requests.get(url, allow_redirects=True))
    def toJson(response):
        return json.loads(response.text, object_hook=lambda d: SimpleNamespace(**d))
        pass
    def packageQuery(request, arguments):
        pass