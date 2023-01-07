 # https://www.nltk.org/howto/stem.html
from nltk.stem.arlstem import ARLSTem


def main():
	stemCorpus("../../../Data/Corpus/CleanCorpus.txt", "../../../Data/Corpus/StemmedCorpus.txt")


def stemCorpus(inputCorpusPath, outputCorpusPath):
	stemmer = ARLSTem()

	inputCorpusFile = open(inputCorpusPath, "r", encoding="utf-8")
	outputCorpusFile = open(outputCorpusPath, "w", encoding="utf-8")

	for line in inputCorpusFile:
		line = line.split(" ")
		stemmedLine = [stemmer.stem(word) for word in line]
		outputCorpusFile.write(" ".join(stemmedLine))

	inputCorpusFile.close()
	outputCorpusFile.close()


if __name__ == "__main__":
	main()
