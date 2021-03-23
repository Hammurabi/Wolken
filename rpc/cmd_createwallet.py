# import commands
import commands
# password helper
from getpass import getpass


# define 'createwallet' command
def parse(cmd, arguments, connection):
    if len(arguments) != 2 and len(arguments) != 3:
        print("error: '"+cmd.name+"' requires atleast one argument.")
    else:
        name    = arguments[1]

        encrypt = False
        if len(arguments) == 3:
            encrypt = arguments[2].lower() == 'true'

        password = ''        
        if encrypt:
            password = getpass('password>')
        
        response = connection.send_request(cmd.name, {'name':name, 'encrypt':encrypt, 'password':password})
        print("alert: server responded with '"+response.response+"'.")
        if response.response == 'failed':
            print("reason: " + response.reason)
        else:
            print("---------------------------------")
            print(response.account)
            print("---------------------------------")

