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
    def send_request(request, arguments):
        url = self.ip + ":" + self.port + "/api?" + package_query(requests, arguments)
        return to_json(requests.get(url, allow_redirects=True))
    def to_json(response):
        return json.loads(response.text, object_hook=lambda d: SimpleNamespace(**d))
    def package_query(request, arguments):
        query = 'request=' + request
        for argument in arguments:
            query = query + "&" + argument[0] + "=" + argument[1]
        return query