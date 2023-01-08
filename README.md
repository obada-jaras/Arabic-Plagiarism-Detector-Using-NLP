# **Arabic Plagiarism Detector Using NLP**
This project includes tools for preprocessing corpus and input text, building a language model, and detecting plagiarism using the NLP language model. The project also features a JavaFX interface for easy use.

![ui](https://user-images.githubusercontent.com/35347949/211175705-c161dbbc-f345-4c92-b7b3-d2aad6b542a9.png)


<br>

## **Detailed Overview**
This section provides a more in-depth explanation of the various features and functions of the Arabic Plagiarism Detector Using NLP.

<br>

### **Part 1: Corpus Cleaning and Text Preprocessing**
To improve the accuracy of the plagiarism detection, the corpus and input text are subjected to a series of preprocessing steps to remove noise and standardize the formatting. These steps include:

- Replacing underscores with spaces to handle hashtags (e.g., #تحيا_فلسطين_حرة becomes تحيا فلسطين حرة).
- Replacing special characters with words, such as:
  - character ﷰ‎ to the sentence "صلى الله عليه وسلم".
  - character  ﷽‎ to the sentence "بسم الله الرحمن الرحيم".
  - character ﷴ‎ to the word "محمد".
- Removing kashida (a type of diacritic used in Arabic writing)[^1].
- Replacing alternative letters that refer to the same letter, such as:
  - letters أ إ آ  to ا. 
  - letters ػ ڪ ک to ك.
- Removing consecutive redundant characters to eliminate repetitions, for example reducing عااااشت فلسطين to عاشت فلسطين.
- Replacing Arabic letters to match colloquial usage:
  - replacing ى with ا.
  - replacing ة with ه.
- Removing non-Arabic characters and Arabic punctuations and numbers.
- Removing stop words (common words that do not contribute to the meaning of a text)[^2] [^3].
- Removing words with only one letter.
- Stemming the text to reduce words to their base form.

These preprocessing steps are necessary to ensure that the language model and plagiarism detection are based on accurate and meaningful data.

> check [this link](https://en.wikipedia.org/wiki/Arabic_script_in_Unicode) to see all the unicodes used.

<br>

### **Part 2: Building the Language Model**
This part of the project reads the cleaned corpus created in the previous step, and generates an n-gram model using a specified number of words (e.g., 4 grams). The model is saved to a CSV file, which contains tokens (1, 2, 3, ... n words) with the n-gram of each one, along with its count and probability.

The probability of each n-gram is calculated based on the Markov Assumption[^4], which states that the probability of a word depends only on the previous n-1 words. The formula for calculating probability using the Markov Assumption is as follows:

P(Wi | W1W2…Wi−1) ≈ P(Wi | Wi−k…Wi−1)

where k is a specified number of previous words (e.g., 2 for a bigram model, 4 in our case). The probability can be calculated as the count of the n-gram (W1...Wi) divided by the count of the previous n-1 words (W1...Wi-1).

This language model is used in the next step to detect plagiarism in the input text or file.

<br>

### **Part 3: Plagiarism Detection**
The Arabic Plagiarism Detector Using NLP includes a user-friendly JavaFX interface that allows users to check a text or CSV file for plagiarism. The interface allows users to enter a text, which will be divided into sentences and processed separately. The output of the program is a score from 0 to 100, with a color code indicating the likelihood of plagiarism (green for low suspicion, red for high suspicion). Suspected stolen words and sentences are highlighted with a color that indicates the fraud rate.

To use the plagiarism detection feature, users can simply enter a text into the interface or select a text or CSV file for analysis. The program will process the input and provide a score and visual representation of the results, making it easy for users to identify potential instances of plagiarism.

The plagiarism detection feature is designed to be self-contained and can be run independently of the other parts of the project. When the program is launched, it reads the CSV file containing the language model generated in the previous step. Then, when the user enters a text to be checked for plagiarism, the input text is preprocessed in the same way as the corpus in the first step.

To calculate the score, the program takes the average of all the tokens in the input text, with the average being weighted based on the gram of each token. This means that tokens with higher grams (e.g., 4 grams) have a higher weight in the calculation. The resulting score is a measure of the similarity between the input text and the language model, with a higher score indicating a higher likelihood of plagiarism.






[^1]: https://en.wikipedia.org/wiki/Kashida
[^2]: https://en.wikipedia.org/wiki/Stop_word
[^3]: https://ar.wikipedia.org/wiki/%D8%A7%D8%B3%D8%AA%D8%A8%D8%B9%D8%A7%D8%AF_%D8%A7%D9%84%D9%83%D9%84%D9%85%D8%A7%D8%AA_%D8%A7%D9%84%D8%B4%D8%A7%D8%A6%D8%B9%D8%A9
[^4]: https://en.wikipedia.org/wiki/Markov_property 