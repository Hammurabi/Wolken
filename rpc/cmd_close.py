# import commands
import commands
# password helper
from getpass import getpass


# define 'close' command
def parse(cmd, arguments, connection):
    if len(arguments) != 1:
        print("error: '"+cmd.name+"' requires only one argument.")
    else:
        password = getpass('password>')
        args     = {'password':password}
        response = connection.send_request('close', args)
        print("alert: server responded with '"+response.response+"'.")
        print("alert: closing down client.")
        quit(0)

