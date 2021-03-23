# import commands
import commands
# password helper
from getpass import getpass

# define 'setnoncewallet' command
def parse(cmd, arguments, connection):
    if len(arguments) != 3:
        print("error: '"+cmd.name+"' requires two arguments.")
    else:
        name    = arguments[1]

        response = connection.send_request('setnoncewallet', {'name':name, 'old':old, 'new':new})
        print("alert: server responded with '"+response.response+"'.")
        if response.response == 'failed':
            print("reason: " + response.reason)

