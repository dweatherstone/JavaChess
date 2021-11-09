import os
import mysql.connector
import re
import datetime
import configparser

def process_file(filepath, debug=False):
    with open(filepath, 'r') as f:
        contents = f.read()
    lines = ['[Event ' + content.strip() for content in contents.strip().split("[Event ") if len(content) > 0]
    if debug:
        print(len(lines))
    games = [(line[:line.find('\n1.')].strip(), line[line.find('\n1.'):].strip()) for line in lines]
    if debug:
        print(len(games))
        print(games[0])
    game_details = []
    for game in games:
        details = [x.replace('[', '').replace(']', '').replace('"', '') for x in game[0].split('\n')]
        details_dict = {}
        for d in details:
            d_split = d.split(' ')
            details_dict[d_split[0]] = ' '.join(x for x in d_split[1:])
        details_dict['Date'] = correct_date_format(details_dict.get('Date', None))
        details_dict['WhiteElo'] = correct_elo_format(details_dict.get('WhiteElo', None))
        details_dict['BlackElo'] = correct_elo_format(details_dict.get('BlackElo', None))
        details_dict['Moves'] = game[1]
        game_details.append(details_dict)

    if debug:
        print(len(game_details))
        print(game_details[0])
    return game_details


def correct_date_format(date_input):
    if date_input is None:
        return None
    date_input = re.sub("[^0-9.?]", "", date_input)
    year = date_input[:4]
    second_part = date_input[5:7]
    third_part = date_input[8:]
    if second_part.isdigit() and third_part.isdigit():
        if int(second_part) > 12:
            check_date = year + '.' + third_part + '.' + second_part
        else:
            check_date = date_input
    else:
        check_date = date_input
    try:
        _ = datetime.datetime.strptime(check_date, "%Y.%m.%d")
    except (ValueError, TypeError):
        check_date = year + '.01.01'
    return check_date


def correct_elo_format(elo_input):
    if elo_input is None:
        return elo_input
    if ':' in elo_input:
        return elo_input[:elo_input.find(':')]
    else:
        return elo_input


def populate_db(game_details, process_game_start=-1, process_game_end=-1, debug=False):
    parser = configparser.ConfigParser()
    config_file = os.path.join(os.getcwd(), '..', 'config.properties')
    parser.read(config_file)
    host = parser.get("config", "db.host")
    user = parser.get("config", "db.user")
    password = parser.get("config", "db.password")
    cnx = mysql.connector.connect(host=host, user=user, password=password, database="ChessGames")
    if process_game_start == -1 or process_game_end == -1:
        for i in range(len(game_details)):
            this_game_details = game_details[i]

            game_args = (this_game_details.get('Event', None),
                         this_game_details.get('Site', None),
                         this_game_details.get('Date', None),
                         this_game_details.get('Round', None),
                         this_game_details.get('White', None),
                         this_game_details.get('Black', None),
                         this_game_details.get('Result', None),
                         this_game_details.get('WhiteElo', None),
                         this_game_details.get('BlackElo', None),
                         this_game_details.get('ECO', None),
                         0)  # This is just a placeholder for the return game_id

            insert_game_cursor = cnx.cursor()
            result_args = insert_game_cursor.callproc("spAddEntireChessGame", game_args)
            game_id = result_args[-1]
            insert_game_cursor.close()

            if debug:
                print("this_game_details['Moves']:", this_game_details['Moves'])

            moves_sql = get_moves_sql(this_game_details['Moves'], game_num=game_id, debug=debug)

            moves_args = (moves_sql,)

            insert_moves_cursor = cnx.cursor()
            insert_moves_cursor.callproc("spAddGameMoves", moves_args)
            insert_moves_cursor.close()
            cnx.commit()
            if debug:
                print("Finished Game", i)
    else:
        # TODO: insert into the various tables.
        # TODO: need to think carefully about not adding in duplicates, especially for players.
        for i in range(process_game_start, process_game_end+1):
            this_game_details = game_details[i]

            game_args = (this_game_details.get('Event', None),
                         this_game_details.get('Site', None),
                         this_game_details.get('Date', None),
                         this_game_details.get('Round', None),
                         this_game_details.get('White', None),
                         this_game_details.get('Black', None),
                         this_game_details.get('Result', None),
                         this_game_details.get('WhiteElo', None),
                         this_game_details.get('BlackElo', None),
                         this_game_details.get('ECO', None),
                         0)   # This is just a placeholder for the return game_id

            insert_game_cursor = cnx.cursor()
            result_args = insert_game_cursor.callproc("spAddEntireChessGame", game_args)
            game_id = result_args[-1]
            insert_game_cursor.close()

            if debug:
                print("this_game_details['Moves']:", this_game_details['Moves'])

            moves_sql = get_moves_sql(this_game_details['Moves'], game_num=game_id, debug=debug)

            moves_args = (moves_sql,)

            insert_moves_cursor = cnx.cursor()
            insert_moves_cursor.callproc("spAddGameMoves", moves_args)
            insert_moves_cursor.close()
            cnx.commit()
            if debug:
                print("Finished Game", i)
    cnx.close()

    return


def get_moves_sql(moves_string, game_num, debug=False):
    moves_string_clean = moves_string[:moves_string.rfind(' ')].strip().replace('\n', ' ').replace('.', ' ').replace('  ', ' ')
    if debug:
        print('Making single line:', moves_string_clean)
    moves = [x.strip() for x in moves_string_clean.split(' ')]
    moves_clean = []
    for i in range(0, len(moves), 3):
        if i + 2 < len(moves):
            moves_clean.append('({}, {}, "{}", "{}")'.format(game_num, moves[i], moves[i + 1], moves[i + 2]))
        else:
            moves_clean.append('({}, {}, "{}", NULL)'.format(game_num, moves[i], moves[i + 1]))

    if debug:
        print('moves:', moves)
        print('moves_clean:', moves_clean)
    query = 'INSERT INTO Move (Game_ID, MoveNum, WhiteMove, BlackMove) VALUES '
    query = query + ', '.join(x for x in moves_clean)
    if debug:
        print(query)
    return query


def full_process_file(filename, source_folder, done_folder, problem_folder, debug=False):
    try:
        source_file = os.path.join(source_folder, filename)
        # gd = process_file(source_file, debug=True)
        # populate_db(gd, debug=True, process_game_start=2887, process_game_end=2888)
        gd = process_file(source_file, debug=debug)
        populate_db(gd, debug=debug)
        # Move the file to show that it's done
        os.rename(source_file, os.path.join(done_folder, filename))
    except Exception as e:
        os.rename(source_file, os.path.join(problem_folder, filename))
        print("FAILED", filename)
        if debug:
            print(e)


if __name__ == '__main__':
    source_folder = os.path.join(os.getcwd(), 'pgn_files')
    done_folder = os.path.join(os.getcwd(), 'done_pgn_files')
    problem_folder = os.path.join(os.getcwd(), 'problem_pgn_files')
    for filename in os.listdir(source_folder):
        print("PROCESSING", filename)
        full_process_file(filename, source_folder, done_folder, problem_folder, debug=True)
        print("DONE", filename)
        print()
