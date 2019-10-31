from selenium import webdriver

options = webdriver.ChromeOptions()
options.add_argument("--user-data-dir=/home/helpdesk/.config/google-chrome")
options.add_argument("--profile-directory=Default")
browser = webdriver.Chrome(options=options, executable_path=r"/usr/local/bin/chromedriver")
browser.get("http://google.com")
