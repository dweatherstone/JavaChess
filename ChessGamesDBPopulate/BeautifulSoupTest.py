from bs4 import BeautifulSoup
import requests
import os
from zipfile import ZipFile


def get_links(html):
    soup = BeautifulSoup(html, 'html.parser')
    player_links = [link.get('href') for link in soup.find_all('a') if str(link.get('href')).endswith('.zip')
                    and str(link.get('href')).startswith('player')]
    player_links = set(player_links)
    print(player_links)
    print(len(player_links))
    download_url_base = 'https://www.pgnmentor.com/'
    for player_link in player_links:
        url = download_url_base + player_link
        filename = player_link.split('/')[1]
        dest_file = os.path.join(os.getcwd(), 'zip_files', filename)
        r = requests.get(url)
        # open method to open a file on your system and write the contents
        with open(dest_file, "wb") as code:
            code.write(r.content)
        print('Done', filename)


def unzip_files():
    all_files = os.listdir(os.path.join(os.getcwd(), 'zip_files'))
    dest_folder = os.path.join(os.getcwd(), 'pgn_files')
    for file in all_files:
        file_url = os.path.join(os.getcwd(), 'zip_files', file)
        with ZipFile(file_url, 'r') as zipObj:
            zipObj.extractall(dest_folder)
        print('Done', file)
    print('\nFinished unzipping')


if __name__ == '__main__':
    url = "https://www.pgnmentor.com/files.html"
    page = requests.get(url).text
    # get_links(page)
    unzip_files()
