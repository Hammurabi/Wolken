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
        self.scheme  = 'http'
    def send_request(self, request, arguments):
        url = self.scheme + "://" + self.ip + ":" + self.port + "/api?" + self.package_query(request, arguments)
        return self.to_json(requests.get(url, allow_redirects=True))
    def to_json(self, response):
        return json.loads(response.text, object_hook=lambda d: SimpleNamespace(**d))
    def package_query(self, request, arguments):
        query = 'request=' + request
        for key in arguments:
            query = query + '&' + str(key) + '=' + str(arguments[key])
        return query