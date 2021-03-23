# import commands
import commands


# define 'node' command
def parse(cmd, arguments, connection):
    if len(arguments) != 3:
        print("error: '"+cmd.name+"' requires two arguments.")
    else:
        if not util.is_valid_ip(arguments[1]):
            print("error: '"+cmd.name+"' requires the first argument to be a valid IP address.")
            pass
        if not arguments[2].isnumeric():
            print("error: '"+cmd.name+"' requires the second argument to be a valid port.")
            pass

        connection.ip   = arguments[1]
        connection.port = arguments[2]

        print("alert: node connection data set to ('"+arguments[1]+":"+arguments[2]+"')")

