## **This branch is for development purposes only**

This branch includes some additional methods and files that were helpful during the development process, but may not be relevant or necessary for the final version of the project.

Note that the normal methods (used in the main project) may be edited, and these edits may not be applied to the dev branch. The most up-to-date version of the methods can be found in the [main branch](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP).

<br>

---
--- 

<br>

# **Additional Files**
- [StopWords.java](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP/blob/dev/src/StopWords.java): This code reads the file that contains the list of stop words, removes duplicates, and saves the words in alphabetical order back to the same file.

## Python Scripts
- [Lemmatizer.py](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP/blob/dev/src/PythonScripts/Lemmatizer.py): This script is used to lemmatize the corpus.
- [LemmatizeSentence.py](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP/blob/dev/src/PythonScripts/LemmatizeSentence.py): This script is used in text preprocessing to lemmatize a sentence. It is called from the Java code using ProcessBuilder.

<br>

# **Additional Methods**
## [Token](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP/blob/dev/src/Token.java) and [Chunk](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP/blob/dev/src/Chunk.java) Classes
- toString(): This method helps with debugging by printing out the contents of the Token or Chunk object.

## [Corpus.java](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP/blob/dev/src/Corpus.java)
- `replaceArabicNumberWithEnglishNumbers()`: This method replaces Arabic numerals with their English counterparts. This method was not used in the final version of the program, as all numbers were deleted.
- `replaceEnglishNumbersWithWord()`: This method replaces all English numerals with a specific word for all numbers. This method was not used in the final version of the program, as all numbers were deleted.
- `printStopWordsLines()`: This method prints out all the lines in which stop words were removed from the Corpus, along with the stop words themselves. The stopWordsLines Hashmap can be filled with the stop word and the line it appears in when removing stop words.
- `printStopWordsLinesSorted(int limit)`: This method prints out all the lines in which stop words were removed from the Corpus, along with the stop words themselves, in alphabetical order. The limit parameter specifies the maximum number of lines to be printed.
- `printStopWordsLinesWithCounts()`: This method prints out the frequency of each stop word that was removed from the Corpus, sorted by frequency.
- `printNonUsedStopWords()`: This method prints out any stop words that did not occur in the corpus.

## [LanguageModel](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP/blob/dev/src/LanguageModel.java)
- `printOrderedHashMapToCSV()`: This method prints out the contents of the LanguageModel hash map ordered to a CSV file.
- `findToken(HashMap<String, Token> hashMap, String text)`: This method searches for a token by its text in a given hash map.
- `maxToken()`: This method returns the token with the highest frequency in the LanguageModel.
- `printModelTable(HashMap<String, Token> modelTable, int limit)`: This method prints out the contents of the hash map in a table format. The limit parameter specifies the maximum number of lines to be printed.
- `printModelTableSortedByCount(HashMap<String, Token> modelTable, int limit)`: This method prints out the contents of the hash map in a table format **sorted by the count**. The limit parameter specifies the maximum number of lines to be printed.
- `printModelTableSortedByGram(HashMap<String, Token> modelTable, int limit)`: This method prints out the contents of the hash map in a table format **sorted by the gram**. The limit parameter specifies the maximum number of lines to be printed.

## [Plagiarism](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP/blob/dev/src/Plagiarism.java)
- `lemmatizeSentence()`: This method lemmatizes a given sentence by calling the external python script [LemmatizeSentence.py](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP/blob/dev/src/PythonScripts/LemmatizeSentence.py).

<br>

--- 

<br>

> Note that the normal methods (used in the main project) may be edited, and these edits may not be applied to the dev branch. The most up-to-date version of the methods can be found in the [main branch](https://github.com/obada-jaras/Arabic-Plagiarism-Detector-Using-NLP).