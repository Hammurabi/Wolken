# import commands
import commands


# define 'node' command
def parse(cmd, arguments, connection):
    if len(arguments) != 1:
        print("error: '"+cmd.name+"' requires only one argument.")
    else:
        response = connection.send_request('close', {'password':pswd})
        print("alert: closing down client.")
        quit(0)

