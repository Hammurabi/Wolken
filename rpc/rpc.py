# import requests to make our lives easier
import requests

# valid commands
# getblock      <hash>
# gettx         <hash>
# getbalance    <address>

def scan_commands():
    # get inputs from the command line
    text        = input(">")

    if not text:
        return lambda x : None

    # parse the command
    arguments   = text.split(" ")

    
def start():
    # enter an infinite loop
    while (True):
        # scan the command line for commands
        commands = scan_commands()