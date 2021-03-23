# import commands
import commands
# password helper
from getpass import getpass

# define 'walletfromdump' command
def parse(cmd, arguments, connection):
    if len(arguments) != 2:
        print("error: '"+cmd.name+"' requires one argument.")
    else:
        address  = arguments[1]

        response = connection.send_request('setnoncewallet', {'address':address})
        print("alert: server responded with '"+response.response+"'.")
        if response.response == 'failed':
            print("reason: " + response.reason)

