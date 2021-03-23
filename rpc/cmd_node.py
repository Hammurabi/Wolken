# import commands
import commands


# define 'node' command
def parse(command, arguments, connection):
    if len(arguments) != 3:
        print("error: '%s' requires two arguments.", command.name)
    else:
        if not util.is_valid_ip(arguments[1]):
            print("error: '%s' requires the first argument to be a valid IP address.", command.name)
            pass
        if not arguments[2].isnumeric():
            print("error: '%s' requires the second argument to be a valid port.", command.name)
            pass

        connection.ip   = arguments[1]
        connection.port = arguments[2]

        print("alert: node connection data set to ('"+arguments[1]+":"+arguments[2]+"')")

