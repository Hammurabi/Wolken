# import commands
import commands


# define 'node' command
def node_parse(command, arguments):
    if len(arguments) != 3:
        print("error: 'auth' requires two arguments.")
    else:
        if not util.is_valid_ip(arguments[1]):
            print("error: 'node' requires the first argument to be a valid IP address.")
            pass
        if not arguments[2].isnumeric():
            print("error: 'node' requires the second argument to be a valid port.")
            pass
        global ip
        global port
        ip      = arguments[1]
        port    = arguments[2]

        print("alert: node connection data set to ('"+arguments[1]+":"+arguments[2]+"')")

