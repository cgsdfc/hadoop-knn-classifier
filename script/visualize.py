import json
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from pandas import DataFrame

sns.set_theme()


def createDataFrame(filename):
    with open(filename) as f:
        jsonData = json.load(f)
    tuningResults = jsonData["tuningResults"]
    Klist = [item["K"] for item in tuningResults]
    meanList = [item["mean"] for item in tuningResults]
    stdList = [item["std"] for item in tuningResults]
    testingResults = jsonData["testingResults"]
    testingMeanList = [item["mean"] for item in testingResults]
    datasetName = jsonData["config"]["dsInfo"]["datasetName"]

    df = DataFrame.from_dict(
        {
            "K": Klist,
            "valid_acc": meanList,
            "test_acc": testingMeanList,
        }
    )
    sns.lineplot(x="K", y="valid_acc", data=df, label="valid")
    sns.lineplot(x="K", y="test_acc", data=df, label="test")
    plt.xlabel("K")
    plt.ylabel("Accuracy")
    plt.xticks(Klist)
    plt.title(f"Experiment on dataset {datasetName}")

    # plt.plot(Klist, meanList)
    plt.show()
    return df


def main():
    fileList = [
        # "data\\car_owners\\result\\finetune-result.json",
        "data\\iris\\result\\finetune-result-2.json",
    ]
    for f in fileList:
        createDataFrame(f)


main()
