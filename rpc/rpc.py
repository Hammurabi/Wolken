# import requests to make our lives easier
import requests

# valid commands
# getblock      <hash>
# gettx         <hash>
# getbalance    <address>

commands = [ new_command('getblock', lambda a : getblock_command(a), labmda a : prase_getblock(a)) ]

class Command:
    pass

def getblock_command(request):
    pass

def new_command(name, value, parse):
    command = Command()
    command.name    = name
    command.value   = value
    command.parse   = parse
    return command

def scan_commands():
    # get inputs from the command line
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
            if command.name == arguments[0]:
                return command.parse(arguments)
def start():
    # enter an infinite loop
    while (True):
        # scan the command line for commands
        commands = scan_commands()