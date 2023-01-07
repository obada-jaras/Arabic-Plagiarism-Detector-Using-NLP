# https://github.com/linuxscout/qalsadiadi
import qalsadi.lemmatizer 


def main():
	lemmatizeCorpus("../../../Data/Corpus/CleanCorpus.txt", "../../../Data/Corpus/LemmatizedCorpus.txt")


def lemmatizeCorpus(inputCorpusPath, outputCorpusPath):
	lemmer = qalsadi.lemmatizer.Lemmatizer()

	inputCorpusFile = open(inputCorpusPath, "r", encoding="utf-8")
	outputCorpusFile = open(outputCorpusPath, "w", encoding="utf-8")

	count = 0
	for line in inputCorpusFile:
		lemmatizedLine = lemmer.lemmatize_text(line)
		outputCorpusFile.write(" ".join(lemmatizedLine))
		print(count)
		count += 1

	inputCorpusFile.close()
	outputCorpusFile.close()


if __name__ == "__main__":
	main()
