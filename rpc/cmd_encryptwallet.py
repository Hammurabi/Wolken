# import commands
import commands
# password helper
from getpass import getpass


# define 'encryptwallet' command
def parse(cmd, arguments, connection):
    if len(arguments) != 2:
        print("error: '"+cmd.name+"' requires one argument.")
    else:
        name     = arguments[1]
        password = getpass('password>')
        
        response = connection.send_request('createwallet', {'name', name, 'encrypt', encrypt, 'password':password})
        print("alert: server responded with '"+response.response+"'.")
        if response.response == 'failed':
            print("reason: " + response.reason)

