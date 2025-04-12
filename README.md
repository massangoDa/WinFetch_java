# WinFetch - ISOダウンローダー
WinFetch は、Microsoft 公式の UUP ファイルを使用し、Windows の ISO ファイルを作成できるツールです。  
Windows 10 / 11 のすべてのバージョンに対応し、Insider Preview のビルドも選択可能です。

## 📸 スクリーンショット  
![image](https://github.com/user-attachments/assets/a208fa98-35eb-4aed-994c-1ffd92e237fe)


## 🚀 主な機能  
- **アーキテクチャの指定** (amd64, arm64)
- **言語選択** (ja-jp)
- **バージョンの指定** (Windows 11, Windows 10)  
- **Insider Preview の選択** (Windows 11 24H2 build)  
- **最新のUUPファイルを自動取得し、ISOを生成**  

## 📖 使い方  
1. ソフトを起動  
2. ダウンロードしたい **アーキテクチャ** を選択 (例: amd64)  
3. ダウンロードしたい **Windows のバージョン** を選択 (例: Windows 11)
4. ダウンロードしたい **言語** を選択 (例: ja-jp)
5. ダウンロードしたい **ビルドバージョン** を選択 (例: Windows 11 24H2)  
6. **ダウンロードボタン** を押す  
7. 完了後、カレントディレクトリ/ISO_FOLDER/ の中に、UUPsフォルダが追加されます。
8. ISO作成ボタンを押して、convert-uup.cmdとbinフォルダをダウンロードします。
9. 完了後、/ISO_FOLDERにisoファイルが作られます。

📂 出力ファイルの例:  
ISO_FOLDER/22621.5116.250307-1750.NI_RELEASE_SVC_PROD2_CLIENTMULTI_X64FRE_JA-JP.iso 

## 💻 動作環境  
- Windows 10 / 11
- (linuxは現在非対応)
