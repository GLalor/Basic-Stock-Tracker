import urllib.request
import json

req = urllib.request.Request(url='https://api.iextrading.com/1.0/stock/wday/price', headers={'User-Agent': 'Mozilla/5.0'}, method='GET')

stock_data = urllib.request.urlopen(req, timeout=1.1)


stock_data = stock_data.read().decode('utf-8')


stock_data = json.loads(stock_data)


print(stock_data)


with open('stockData.json', 'w') as out:
    json.dump(stock_data, out)