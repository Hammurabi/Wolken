# import commands
import commands
# password helper
from getpass import getpass

# define 'loadwallet' command
def parse(cmd, arguments, connection):
    if len(arguments) != 2:
        print("error: '"+cmd.name+"' requires one argument.")
    else:
        timeout     = arguments[1]
        password    = getpass('password>')

        response = connection.send_request(cmd.name, {'timeout':timeout, 'password':password})
        print("alert: server responded with '"+response.response+"'.")
        if response.response == 'failed':
            print("reason: " + response.reason)

