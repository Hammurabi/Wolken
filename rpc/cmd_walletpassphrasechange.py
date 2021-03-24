# import commands
import commands
# password helper
from getpass import getpass

# define 'walletpassphrasechange' command
def parse(cmd, arguments, connection):
    if len(arguments) != 2:
        print("error: '"+cmd.name+"' requires one argument.")
    else:
        name    = arguments[1]
        old     = getpass('old password>')
        new     = getpass('new password>')
        confirm = getpass('confirm new>')
        if password != password2:
            print('error: please make sure you typed the same password.')
            return

        response, content = connection.send_request(cmd.name, {'name':name, 'old':old, 'new':new})
        print("alert: server responded with '"+response.response+"'.")
        if response.response == 'failed':
            print("reason: " + response.reason)
        else:
            print(content)

