# import commands
import commands
# password helper
from getpass import getpass


# define 'createwallet' command
def parse(cmd, arguments, connection):
    if len(arguments) != 2 and len(arguments) != 3:
        print("error: '"+cmd.name+"' requires atleast one argument.")
    else:
        encrypt = arguments[2].lower() == 'true'
        password = getpass('password>')
        response = connection.send_request('close', {'password':password})
        print("alert: server responded with '"+response.response+"'.")
        print("alert: closing down client.")
        quit(0)

