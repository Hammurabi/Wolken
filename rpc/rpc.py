# import requests to make our lives easier
import requests

# valid commands
# getblock      <hash>
# gettx         <hash>
# getbalance    <address>
# quit

def getblock_parse(command, arguments):
    # check correct amount of arguments exists
    if len(arguments < 2):
        print("error: 'getblock' command requires a minimum of two arguments.")
        pass

    # check that the block-id is base16 encoded
    if is_base16_encoded(arguments[1]):
        print("error: 'getblock' command requires second argument to be base16 encoded.")
        pass

    # getblock <hash>
    # getblock <hash> includeTx txAsHash includeEv evAsHash format
    if len(arguments) > 2 and len(arguments) < 7:
        print("error: 'getblock' command missing arguments.")
        pass

class Command:
    pass


def new_command(name, parse):
    command = Command()
    command.name    = name
    command.parse   = parse
    return command
    

commands_list = [ new_command('getblock', getblock_parse) ]
base16   = ['0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f']
base58   = ['1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z']

def is_base16_encoded(text):
    for c in text.lower():
        for x in base16:
            if c == x:
                return True
    return False

def is_base58_encoded(text):
    for c in text.lower():
        for x in base58:
            if c == x:
                return True
    return False

def start():
    # enter an infinite loop
    while (True):
        # scan the command line for commands
        text        = input(">")
        # this shouldn't happen
        if not text:
            return lambda x : None
        # parse the command
        arguments   = text.split(" ")
        # check the length
        if len(arguments) > 0:
            # test against known commands
            for command in commands_list:
                # if the command name matches the first argument
                if command.name == arguments[0]:
                    # attempt to parse the command
                    command.parse(arguments)

# start the loop
start()