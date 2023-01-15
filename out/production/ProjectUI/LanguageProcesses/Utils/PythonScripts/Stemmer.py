 # https://www.nltk.org/howto/stem.html
from nltk.stem.arlstem import ARLSTem


def main():
	stemCorpus("../../../Data/Corpus/CleanCorpus.txt", "../../../Data/Corpus/StemmedCorpus.txt")

# Function that takes a corpus file, and writes a stemmed corpus file to the output path
# Uses the ARLSTem stemmer to stem words in the corpus
# Inputs:
#   inputCorpusPath - path to the input corpus file
#   outputCorpusPath - path to the output corpus file
# Outputs:
#   None
# Side-effects:
#   Creates a stemmed corpus file at the output path
def stemCorpus(inputCorpusPath, outputCorpusPath):
	stemmer = ARLSTem()

	inputCorpusFile = open(inputCorpusPath, "r", encoding="utf-8")
	outputCorpusFile = open(outputCorpusPath, "w", encoding="utf-8")

	for line in inputCorpusFile:
		try:
			words = line.split(" ")
			stemmedWords = [stemmer.stem(word) for word in words]
			outputCorpusFile.write(" ".join(stemmedWords))
		except:
			print("Error when stemming line: ", line)
			continue

	inputCorpusFile.close()
	outputCorpusFile.close()


if __name__ == "__main__":
	main()
