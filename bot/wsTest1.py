import time
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
import sys


phone="+573227309677" # my id phone
msg="msg default" # when not change it, the value default send that
url1 = "https://web.whatsapp.com/"
url2 = "https://web.whatsapp.com/send?phone={}&text={}&source=&data=#"
url2 = url2.format(phone, msg)
jsUrl = "window.location.href = \"{}\";"
jsUrl = jsUrl.format(url2)
# this line is to get button where is localate the send action
jsSendMsg = 'btn=document.getElementsByTagName("button");for(i=0;i<btn.length;i++){if(btn[i].className=="_3M-N-"){btn[i].click();}}'

options = webdriver.ChromeOptions()
options.add_argument("--user-data-dir=/home/helpdesk/.config/google-chrome")
options.add_argument("--profile-directory=Default")
browser = webdriver.Chrome(options=options, executable_path=r"/usr/local/bin/chromedriver")
# active line if you not have session into whatsapp
# browser.get(url1)
# time.sleep(5)
# active line if you need active access to QR code
# browser.execute_script(jsUrl)

# comment this line when you before uncomment the lines to connect your WhatsApp with QR
browser.get(url2)

# allow wait you load the target phone the cat project
time.sleep(10)
# active event to send message
browser.execute_script(jsSendMsg)
# wait to make sure the event is trigger
time.sleep(1)
# active line if you need close the window when finish all events
#driver.quit()
