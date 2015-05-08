# PrisonArchitect-translation
We are a group of people who want to translate in other languages the game "Prison Architect".

The current translations are:
- Italian

## Folder Structure
|- English - Original File to traslate
|  |- base-language.txt - main language phrases (complete file)
|  |- base-language_new_key.txt - new and changed phrases from base-language previous version
|  |- tablets.txt - other traslate (complete file)
|  \- tablets_new_key.txt - new key and change key from tables previous version
|- Laguages - One folder for language
|  |- data
|  |  |- language
|  |  |  |- base-language_new_key_translate.txt - traslation of the base-language_new_key file of english version
|  |  |  |- base-language.txt - traslation of the same file of the english version
|  |  |  |- tablets_key_traslate.txt - traslation of the tablets_new_key file of english version
|  |  |  \- tablets.txt - traslation of the same file of the english version
|  |  |- biographies.txt
|  |  |- fonts - Fonts for the writings
|  |  |- letter.png - introdution letter is a mage to change
|  |  |- loading.png
|  |  \- qualification.bmp
|  |- thumbnail.png
|  \- manifest.txt
|- utility - Contain utility for text conversion
|- LICENSE - Project license
\- README.md - This file


## Procedure to generate traslate of base-language.txt
1. Prepare base-language_new_key.txt with the difference between old base-language.txt and new base-language.txt
	./bin/MultiCompare -c -e English/base-language.txt -o English/base-language_new_key.txt English/base-language-<previous version>.txt
	
2. Human traslate the file base-language_new_key.txt and rename it in base-language_new_key_translate.txt

3. Create new traslated base-language.txt for a wanted language
	./bin/MultiCompare -m -e English/base-language.txt -o <language>/data/language/base-language-<new version>.txt <language>/data/language/base-language.txt <language>/data/language/base-language_new_key_translate.txt
	
4. Reanme <language>/data/language/base-language-<new version>.txt in <language>/data/language/base-language.txt (this process need to remove previous file <language>/data/language/base-language.txt)

