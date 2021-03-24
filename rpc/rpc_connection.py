# import requests to make our lives easier
import requests
import json
from types import SimpleNamespace

class rpc_noconnection:
    def __init__(self, reason):
        self.response = 'failed'
        self.reason   = reason
# define a 'connection' class to hold all the connection information
class rpc_connection:
    def __init__(self):
        self.ip      = 'localhost'
        self.port    = '12560'
        self.token   = ''
        self.scheme  = 'http'
    def send_request(self, request, arguments):
        url = self.scheme + "://" + self.ip + ":" + self.port + "/api?" + self.package_query(request, arguments)
        try:
            return self.to_json(requests.get(url, allow_redirects=True))
        except:
            return rpc_noconnection('failed to send request to server.'), ''
    def post_request(self, request, arguments):
        url = self.scheme + "://" + self.ip + ":" + self.port + "/api?" + self.package_query(request, arguments)
        try:
            return self.to_json(requests.get(url, allow_redirects=True))
        except:
            return rpc_noconnection('failed to send request to server.'), ''
    def to_json(self, response):
        obj     = json.loads(response.text, object_hook=lambda d: SimpleNamespace(**d))
        dump    = ''
        if hasattr(obj, 'content'):
            dump= json.dumps(json.loads(response.text)['content'], indent=4)
        return obj, dump
    def package_query(self, request, arguments):
        query = 'request=' + request
        for key in arguments:
            query = query + '&' + str(key) + '=' + str(arguments[key])
        return query