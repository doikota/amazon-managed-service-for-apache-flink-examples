import datetime
import json
import random
import time
import boto3

STREAM_NAME = "InputStream"


def get_data():
    # Generate random stock data as JSON
    return {
        "eventTime": datetime.datetime.now().isoformat(),
        "ticker": random.choice(["AAPL", "AMZN", "MSFT", "INTC", "TBV"]),
        "price": round(random.random() * 100, 2),
    }


def generate(stream_name, kinesis_client):
    while True:
        data = get_data()
        # data をJSON形式にする
        print(data)
        kinesis_client.put_record(
            StreamName=stream_name, Data=json.dumps(data), PartitionKey=data.get("ticker")
        )
        # sleep for 10 second
        time.sleep(10)


if __name__ == "__main__":
    generate(STREAM_NAME, boto3.client("kinesis"))
