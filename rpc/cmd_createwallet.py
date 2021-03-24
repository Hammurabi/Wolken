# import commands
import commands
import json
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
            password2 = getpass('confirm>')
            if password != password2:
                print('error: please make sure you typed the same password.')
                return
        
        response = connection.send_request(cmd.name, {'name':name, 'encrypt':encrypt, 'password':password})
        print("alert: server responded with '"+response.response+"'.")
        if response.response == 'failed':
            print("reason: " + response.reason)
        else:
            print("---------------------------------")
            print(json.dumps(response.content, indent=4))
            print("---------------------------------")

