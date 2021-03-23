# import requests to make our lives easier
import requests
# import socket to do IP address checks
import util
# import connection
import rpc_connection

# define a basic command class
class Command:
    pass

# define a basic command object constructor
def new_command(name, parse):
    command         = Command()
    command.name    = name
    command.parse   = parse
    return command

# define the commandsmanager class
# this class will keep track of
# commands and argument parsing
class CommandsManager:
    def __init__(self):
        self.commands = []
    def register(self, command):
        self.commands.append(command)
    def parse(self, arguments, connection):
        # check the length
        if len(arguments) > 0:
            # maintenance variable to keep track of the state
            is_parsed = False
            # test against known commands
            for command in commands_list:
                # if the command name matches the first argument
                if command.name == arguments[0]:
                    # attempt to parse the command
                    command.parse(command, arguments, connection)
                    # inform the program that we have parsed or attempted to parse the command
                    is_parsed = True
            # send an error if the command was not parsed
            if not is_parsed:
                print("error: command '" + arguments[0] + "' is not a recognized command.")
            # return is_parsed to inform the caller whether this operation succeeded or not
            return is_parsed
        else:
            return False