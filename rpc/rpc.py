# import socket to do IP address checks
import util
# import commands
import commands

# valid commands
# node          <ip> <port>
# getblock      <hash>
# gettx         <hash>
# getbalance    <address>
# exit
# quit

ip      = 'localhost'
port    = '12560'
token   = ''

def start():
    # create a command manager
    # enter an infinite loop
    while (True):
        # scan the command line for commands
        text        = input(">")
        # this shouldn't happen
        if not text:
            return lambda x : None
        # parse the command
        arguments   = text.split(" ")
# define 'connect' command
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
# define 'exit' command
def exit_parse(command, arguments):
    print("alert: terminating process")
    quit()
# define 'quit' command
def quit_parse(command, arguments):
    print("alert: terminating process")
    requests.get(ip + port + "/quit")
    quit()
# define 'getblock' command
def getblock_parse(command, arguments):
    # check correct amount of arguments exists
    if len(arguments < 2):
        print("error: 'getblock' command requires a minimum of two arguments.")
        pass

    # check that the block-id is base16 encoded
    if util.is_base16_encoded(arguments[1]):
        print("error: 'getblock' command requires second argument to be base16 encoded.")
        pass

    # getblock <hash>
    # getblock <hash> includeTx txAsHash includeEv evAsHash format
    if len(arguments) > 2 and len(arguments) < 7:
        print("error: 'getblock' command missing arguments.")
        pass

# define a basic command object constructor
def new_command(name, parse):
    command = Command()
    command.name    = name
    command.parse   = parse
    return command
    

# define a basic command list
commands_list = [   
                    new_command('node', node_parse),
                    new_command('exit', exit_parse),
                    new_command('quit', quit_parse),
                    new_command('getblock', getblock_parse)
                    ]

# define 'base16' characters
base16   = ['0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f']

# define 'base58' characters
base58   = ['1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z']

# start the loop
start()