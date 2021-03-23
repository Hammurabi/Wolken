# import commands
import commands
# password helper
from getpass import getpass

# define 'walletpassphrase' command
def parse(cmd, arguments, connection):
    if len(arguments) != 1:
        print("error: '"+cmd.name+"' does not take arguments.")
    else:
        old     = getpass('old password>')
        new     = getpass('new password>')

        response = connection.send_request('walletpassphrase', {'name':name, 'old':old, 'new':new})
        print("alert: server responded with '"+response.response+"'.")
        if response.response == 'failed':
            print("reason: " + response.reason)

