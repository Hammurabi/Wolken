# import requests to make our lives easier
import requests

def start():
    # enter an infinite loop
    while (True):
        # scan the command line for commands
        commands = scan_commands()