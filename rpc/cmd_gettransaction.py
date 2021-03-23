# import commands
import commands
# password helper
from getpass import getpass

# define 'signtransaction' command
def parse(cmd, arguments, connection):
    if len(arguments) != 2:
        print("error: '"+cmd.name+"' requires one argument.")
    else:
        transaction = arguments[1]

        response    = connection.send_request(cmd.name, {'transaction':transaction})
        print("alert: server responded with '"+response.response+"'.")
        if response.response == 'failed':
            print("reason: " + response.reason)
        else:
            print("---------------------------------")
            print(response.response)
            print("---------------------------------")

