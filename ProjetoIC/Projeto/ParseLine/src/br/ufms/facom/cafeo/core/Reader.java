package br.ufms.facom.cafeo.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Stack;
import java.util.TimeZone;

import br.ufms.facom.cafeo.utils.ManipulationUtils;

public class Reader {

	static File f = new File("C:\\Users\\Vini\\Desktop\\testando.txt");
	static File r = new File("C:\\Users\\Vini\\Desktop\\resultado.txt");
	static File s = new File("C:\\Users\\Vini\\Desktop\\semcomentario.txt");
	static BufferedReader br = null;
	static String line = "";
	static String code = "";


	public static void main(String[] args) throws IOException {

		ManipulationUtils.writeinFile(r, foo(f));
	}

	private static ArrayList<String> foo(File f) {

		File TEMP = f;

		Scanner input = null;
		
		try {

			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				
				code += line + "\n";

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try (FileWriter writer = new FileWriter(s); 
				BufferedWriter bw = new BufferedWriter(writer)) {

			bw.write(Vector.removeComments(code).toString());

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}

		

		ArrayList<String> varLine = new ArrayList<String>();
		ArrayList<String> fileLines = new ArrayList<String>();
		Stack<String> variabilities = new Stack<String>();

		try {
			input = new Scanner(s);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (input.hasNext())
			fileLines.add(input.nextLine());

		if (!fileLines.isEmpty())
			try {
				foo_helper(fileLines, 1, variabilities, varLine);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println(f.getAbsolutePath());
				System.err.println(e.getMessage());
				// System.err.println("Deu ruim!");
			}

		input.close();

		return varLine;
	}

	public static String[] removeTheElement(String[] arr, int index) {

		if (arr == null || index < 0 || index >= arr.length) {

			return arr;
		}

		String[] anotherArray = new String[arr.length - 1];

		System.arraycopy(arr, 0, anotherArray, 0, index);

		System.arraycopy(arr, index + 1, anotherArray, index, arr.length - index - 1);

		return anotherArray;
	}

	private static void foo_helper(ArrayList<String> fileLines, int lineNumber, Stack<String> variabilities,
			ArrayList<String> varLine) throws Exception {

		String line = "";
		String var = "";
		int tpos = 0;
		String tru = "TRUE";
		boolean bar = false;
		boolean vari = false;
		boolean tab = false;
		String varelif = "";
		int variCount = 0;
		int aux = 0;
		String lastVarAux = "";

		if (lineNumber <= fileLines.size()) {

			for (String l : fileLines) {
				aux++;
				tpos = 0;

				if (variCount == -1) {
					System.out.println("debug");
				}

				line = fileLines.get(lineNumber - 1);

				l = l.replace("\t", " ");
				l = l.replace(",", "virg");
				
				//l = l.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)|(?::-)","");

				String[] tokens = l.trim().split(" ");
				String[] tokensTab = l.trim().split("	");
				String[] tokensDE = l.trim().split("  ");

				int foraux = tokens.length;
				for (int x = 0, y = 0; x <= y; x++) {
					for (int n = 0; n < tokens.length; n++) {
						if (n >= tokens.length) {
							continue;
						}
						if (tokens.length == 1 || tokens[n] == "if" || tokens[n] == "ifdef" || tokens[n] == "ifndef"
								|| tokens[n] == "elif" || tokens[n] == "else") {
							break;
						}
						if (tab) {
							tokens = removeTheElement(tokens, n);
							y++;
							continue;	
						}
						if (tokens[n].contains(" ") || tokens[n].equals("")) {
							y++;
							tokens = removeTheElement(tokens, n);
						}
						if (tokens[n].contains("	/*")) {
							tab = true;
						}



					}
					tab = false;
				}
				if (bar) {
					var += l;
					if (!(tokens[tokens.length - 1].contains("\\"))) {
						bar = false;
					}

					continue;
				}
				// PROCURA "IF DEFINED"

				if (tokens.length >= 2 && tokens[0].equals("#if") && tokens[1].equals("defined") || tokens.length >= 3
						&& tokens[0].contains("#") && tokens[1].equals("if") && tokens[2].equals("defined")) {

					if (tokens.length >= 3 && tokens[0].contains("#") && tokens[1].equals("if")
							&& tokens[2].equals("defined")) {
						if (variabilities.size() > 0) {
							if (variabilities.peek() == "NULL") {
								variabilities.pop();
								variabilities.push(var);
							}
						}
						tpos = 1;
					}
					if (tokens[tokens.length - 1].contains("\\")) {
						bar = true;
					}

					if (variabilities.isEmpty()) {
						vari = true;
						variCount++;
						for (int i = tpos + 2; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var = tokens[i] + " ";
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}

//					else if (variabilities.peek() == nu)
//						variabilities.pop();

					else if (variabilities.peek() == var && variabilities.peek() != "TRUE") {
						vari = true;
						variCount++;
						var = var + "& ";
						for (int i = tpos + 2; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var += tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else {

						vari = true;
						variCount++;
						if (variabilities.peek() == "TRUE") {
							var = "";
						}

						for (int i = tpos + 2; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var = var + tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}

				}

				// PROCURA "IFNDEF"
				else if (tokens[tpos].startsWith("#ifndef")
						|| tokens.length >= 2 && tokens[0].contains("#") && tokens[1].equals("ifndef")) {

					if (tokens[0].contains("#") && tokens[1].equals("ifndef")) {
						if (variabilities.size() > 0) {
							if (variabilities.peek() == "NULL") {
								variabilities.pop();
								variabilities.push(var);
							}
						}
						tpos = 1;
					}
					if (tokens[tokens.length - 1].contains("\\")) {
						bar = true;
					}

					if (variabilities.isEmpty()) {
						vari = true;
						variCount++;
						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("\t/*")) {
								String[] commentTab = tokens[i].split("	");
								for (int t = 0; t < commentTab.length; t++) {
									if (commentTab[t].contains("/*") || commentTab[t].contains("//")) {
										if (tokens[i].contains("	")) {
											String[] tabIn = tokens[i].trim().split("	");
											var += var + tabIn[0];
										}
										break;
									}
									var = "!(" + commentTab[i] + ") ";
									lastVarAux += commentTab[i];
								}
								break;
							} else if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							} else {
								var = "!(" + tokens[i] + ") ";
								lastVarAux += tokens[i];
							}

						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}

					else if (variabilities.peek() == var && variabilities.peek() != "TRUE") {
						vari = true;
						variCount++;
						var = var + "& !(";
						lastVarAux = "!(";
						if (tokens.length == 1) {
							for (int i = tpos + 1; i < tokensTab.length; i++) {

								if (tokensTab[i].contains("/*") || tokensTab[i].contains("//")) {

									break;
								} else {
									var += tokensTab[i];
									lastVarAux += tokensTab[i];
								}
							}
						} else {

							for (int i = tpos + 1; i < tokens.length; i++) {

								if (tokens[i].contains("\t/*")) {
									String[] commentTab = tokens[i].split("	");
									for (int t = 0; t < commentTab.length; t++) {
										if (commentTab[t].contains("/*") || commentTab[t].contains("//")) {
											if (tokens[i].contains("	")) {
												String[] tabIn = tokens[i].trim().split("	");
												var += var + tabIn[0];
											}
											break;
										} else {
											var += commentTab[t];
											lastVarAux += commentTab[t];
										}
									}
									break;
								} else if (tokens[i].contains("/*") || tokens[i].contains("//")) {
									if (tokens[i].contains("	")) {
										String[] tabIn = tokens[i].trim().split("	");
										var += var + tabIn[0];
									}
									break;
								} else {
									var += tokens[i];
									lastVarAux += tokens[i];
								}

							}
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						lastVarAux += ") ";
						var += ") ";
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else if (variabilities.peek() == "NULL") {
						variabilities.pop();
						variabilities.push(var);
						vari = true;
						variCount++;
						var = var + "& !(";
						lastVarAux = "!(";
						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var += tokens[i];
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						lastVarAux += ") ";
						var += ") ";
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else {

						vari = true;
						variCount++;
						if (variabilities.peek() == "TRUE") {
							var = "";
						}
						if (tokens.length == 1) {
							for (int i = tpos + 1; i < tokensTab.length; i++) {

								if (tokensTab[i].contains("/*") || tokensTab[i].contains("//")) {

									break;
								} else {
									var += tokensTab[i];
									lastVarAux += tokensTab[i];
								}
							}
						} else if (tokens[1].contentEquals("")) {
							for (int i = tpos + 1; i < tokensDE.length; i++) {
								if (tokens[i].contains("/*") || tokens[i].contains("//")) {
									if (tokens[i].contains("	")) {
										String[] tabIn = tokens[i].trim().split("	");
										var += var + tabIn[0];
									}
									break;
								}
								var = "!(" + var + tokensDE[i] + ") ";
								lastVarAux += tokensDE[i];
							}
						} else {
							for (int i = tpos + 1; i < tokens.length; i++) {
								if (tokens[i].contains("/*") || tokens[i].contains("//")) {
									if (tokens[i].contains("	")) {
										String[] tabIn = tokens[i].trim().split("	");
										var += var + tabIn[0];
									}
									break;
								}

								var = "!(" + var + tokens[i] + ") ";
								lastVarAux += tokens[i];
							}
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}

				}
				// PROCURA "IF" OU "IFDEF"
				else if (tokens[tpos].contains("#ifdef") || tokens[tpos].contains("#if")
						|| tokens.length >= 2 && tokens[0].contains("#") && tokens[1].equals("if")
						|| tokens.length >= 2 && tokens[0].contains("#") && tokens[1].equals("ifdef")) {

					if (tokens[0].contains("#") && tokens[1].equals("if")
							|| tokens[0].contains("#") && tokens[1].equals("ifdef")) {
						if (variabilities.size() > 0) {
							if (variabilities.peek() == "NULL") {
								variabilities.pop();
								variabilities.push(var);
							}
						}
						tpos = 1;
					}
					if (tokens[tokens.length - 1].contains("\\")) {
						bar = true;
					}

					if (variabilities.isEmpty()) {
						vari = true;
						variCount++;
						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var = tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add("NULL");
						System.out.println(variabilities + " " + aux);
					}

					else if (variabilities.peek() == var && variabilities.peek() != "TRUE") {
						vari = true;
						variCount++;
						var += "& ";
						if (tokens.length == 1) {
							for (int i = tpos + 1; i < tokensTab.length; i++) {
								if (tokensTab[i].contains("/*")) {

									break;
								}
								var += tokensTab[i] + " ";
								lastVarAux += tokensTab[i];
							}
						} else {
							for (int i = tpos + 1; i < tokens.length; i++) {
								if (tokens[i].contains("/*") || tokens[i].contains("//")) {
									if (tokens[i].contains("	")) {
										String[] tabIn = tokens[i].trim().split("	");
										var += var + tabIn[0];
									}
									break;
								}
								var += tokens[i] + " ";
								lastVarAux += tokens[i];
							}
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);

					} else if (var == "") {
						vari = true;
						variCount++;

						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}

							var = var + tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);

					} else if (variabilities.peek() == "NULL") {
						variabilities.pop();
						variabilities.push(var);
						vari = true;
						variCount++;
						var += "& ";
						if (tokens.length == 1) {
							for (int i = tpos + 1; i < tokensTab.length; i++) {
								if (tokensTab[i].contains("/*")) {

									break;
								}
								var += tokensTab[i] + " ";
								lastVarAux += tokensTab[i];
							}
						} else {
							for (int i = tpos + 1; i < tokens.length; i++) {
								if (tokens[i].contains("/*") || tokens[i].contains("//")) {
									if (tokens[i].contains("	")) {
										String[] tabIn = tokens[i].trim().split("	");
										var += var + tabIn[0];
									}
									break;
								}
								var += tokens[i] + " ";
								lastVarAux += tokens[i];
							}
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);

					} else {
						vari = true;
						variCount++;
						if (variabilities.peek() == "TRUE") {
							var = "";
						}
						if (tokens.length == 1) {
							for (int i = tpos + 1; i < tokensTab.length; i++) {
								if (tokensTab[i].contains("/*")) {

									break;
								}
								var += tokensTab[i] + " ";
								lastVarAux += tokensTab[i];
							}
						} else {
							for (int i = tpos + 1; i < tokens.length; i++) {
								if (tokens[1].contains("/*")) {
									String[] commentJunto = tokens[1].split("/*");
									for (int g = 0; g < commentJunto.length; g++) {
										if (commentJunto[g].contains("*")) {
											break;
										}
										var += commentJunto[g];

									}

									break;
								}
								if (tokens[i].contains("/*") || tokens[i].contains("//")) {
									if (tokens[i].contains("	")) {
										String[] tabIn = tokens[i].trim().split("	");
										var += var + tabIn[0];
									}
									break;
								}
								var = var + tokens[i] + " ";
								lastVarAux += tokens[i];
							}
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}

						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}

				}
				// PROCURA "ELIF"
				else if (tokens[tpos].startsWith("#elif")
						|| tokens.length >= 2 && tokens[0].contains("#") && tokens[1].equals("elif")) {

//					if (variabilities.peek() == nu)
//						variabilities.pop();

					if (tokens[0].contains("#") && tokens[1].equals("elif")) {
						if (variabilities.size() > 0) {
							if (variabilities.peek() == "NULL") {
								variabilities.pop();
								variabilities.push(var);
							}
						}
						tpos = 1;
					}

					if (tokens[tokens.length - 1].contains("\\")) {
						bar = true;
					}

					if (variabilities.size() > 1 && variabilities.get(variabilities.size() - 2) != "TRUE"
							&& variabilities.get(variabilities.size() - 2) != "NULL") {
						varelif = var;
						var = variabilities.get(variabilities.size() - 2) + "& ";

					} else {
						varelif = var;
						var = "";
					}

					if (variabilities.isEmpty()) {
						vari = true;
						variCount++;
						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var += tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}

					else if (variabilities.peek() == varelif) {
						vari = true;
						variCount++;
						variabilities.pop();
						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var += tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else {

						vari = true;
						variCount++;
						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var += var + tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);

					}
				}
				// PROCURA "ELSE IF"
				else if (tokens.length >= 2 && tokens[tpos].startsWith("#else") && tokens[tpos].startsWith("if")
						|| tokens.length >= 3 && tokens[0].contains("#") && tokens[1].contains("else")
								&& tokens[2].contains("if")) {

					if (tokens.length >= 3 && tokens[0].contains("#") && tokens[1].contains("else")
							&& tokens[2].contains("if")) {
						if (variabilities.size() > 0) {
							if (variabilities.peek() == "NULL") {
								variabilities.pop();
								variabilities.push(var);
							}
						}
						tpos = 2;
					}

					if (variabilities.size() > 1 && variabilities.get(variabilities.size() - 2) != "TRUE"
							&& variabilities.get(variabilities.size() - 2) != "NULL") {
						varelif = var;
						var = variabilities.get(variabilities.size() - 2) + "& ";

					} else {
						varelif = var;
						var = "";
					}

					if (tokens[tokens.length - 1].contains("\\")) {
						bar = true;
					}

					if (variabilities.isEmpty()) {
						vari = true;
						variCount++;
						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var += tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}

//					else if (variabilities.peek() == nu)
//						variabilities.pop();

					else if (variabilities.peek() == varelif) {
						vari = true;
						variCount++;
						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var += tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else {

						vari = true;
						variCount++;
						for (int i = tpos + 1; i < tokens.length; i++) {
							if (tokens[i].contains("/*") || tokens[i].contains("//")) {
								if (tokens[i].contains("	")) {
									String[] tabIn = tokens[i].trim().split("	");
									var += var + tabIn[0];
								}
								break;
							}
							var = var + tokens[i] + " ";
							lastVarAux += tokens[i];
						}
						if (var.contains("defined")) {
							var = var.replace("defined", "");
						}
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}
				}
				// PROCURA ELSE
				else if (tokens[tpos].startsWith("#else")
						|| tokens.length >= 2 && tokens[0].startsWith("#") && tokens[1].equals("else")) {
					String[] tokenElse = var.trim().split(" & ");
					vari = true;
					if (variabilities.peek() == "NULL" && variabilities.size() <= 2) {
						variabilities.pop();
						String lastVar = tokenElse[tokenElse.length - 1];
						var = "!(" + lastVar + ")";
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else if (variabilities.peek() == "NULL") {
						variabilities.pop();
						String lastVar = tokenElse[tokenElse.length - 1];
						variabilities.pop();
						var = variabilities.peek() + "& !(" + lastVar + ")";
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else if (variabilities.peek().contains(" & ")) {
						variabilities.pop();
						var = variabilities.peek() + "& !(" + (tokenElse[tokenElse.length - 1]) + ")";

						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else if (variabilities.peek() == var && variabilities.peek() != "TRUE"
							&& variabilities.peek() != "NULL") {

						var = "!(" + (tokenElse[tokenElse.length - 1]) + ")";
						variabilities.pop();
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else if (variabilities.get(variabilities.size() - 2) == "TRUE") {
						variabilities.pop();
						var = "!(" + (tokenElse[tokenElse.length - 1]) + ")";
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else {
						variabilities.pop();
						var = variabilities.peek() + "& !(" + (tokenElse[tokenElse.length - 1]) + ")";
						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}

				}

				// PROCURA "ENDIF"
				else if (tokens[tpos].startsWith("#endif")
						|| tokens.length >= 2 && tokens[0].startsWith("#") && tokens[1].contains("endif")) {

					
					//if(variCount >0) {
						variCount--;
					//}
					

					if (variabilities.size() == 1) {
						vari = false;
						var = variabilities.get(variabilities.size() - 1);
						if (!variabilities.isEmpty())
							variabilities.pop();

						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else {
						vari = false;
						var = variabilities.get(variabilities.size() - 2);
						if (!variabilities.isEmpty())
							variabilities.pop();

						variabilities.push("NULL");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
						variabilities.pop();
					}
				}
				// VERIFICA SE A PILHA ESTA VAZIA
				else if (variabilities.isEmpty()) {

					variabilities.push("TRUE");
					varLine.add(variabilities.peek());
					System.out.println(variabilities + " " + aux);
				}
				// VERIFICA SE ESTA DENTRO DE UMA VARIABILIDADE
				else if (variCount > 0) {

					if (tokens.length == 0 || (tokens.length == 1
							&& (tokens[tpos].trim().isEmpty() || tokens[tpos].trim().isEmpty()))) {
						if (variabilities.peek() == "NULL")
							variabilities.pop();
						if (variabilities.isEmpty() || variabilities.peek() != var) {
							variabilities.push(var);
						}

						variabilities.push("EMPTY LINE");
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else {

						if (variabilities.peek() == "NULL")
							variabilities.pop();
						if (variabilities.isEmpty() || variabilities.peek() != var) {
							variabilities.push(var);
						}
						varLine.add(var);
						System.out.println(variabilities + " " + aux);
					}
				}

				// VERIFICA LINHA EM BRANCO
				else if (tokens.length == 0
						|| (tokens.length == 1 && (tokens[tpos].trim().isEmpty() || tokens[tpos].trim().isEmpty()))) {

					if (variabilities.peek() == "NULL")
						variabilities.pop();

					variabilities.push("EMPTY LINE");
					varLine.add(variabilities.peek());
					System.out.println(variabilities + " " + aux);

				}

				else {

					if (vari && variabilities.peek() != "NULL") {
						variabilities.pop();
						variabilities.push(var);
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else if (vari) {
						variabilities.pop();
						variabilities.push(var);
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else if (variabilities.peek() != tru) {
						variabilities.pop();

						if (variabilities.empty()) {
							variabilities.push("TRUE");
						}
						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					} else {

						varLine.add(variabilities.peek());
						System.out.println(variabilities + " " + aux);
					}

				}
				if (variabilities.peek() == "EMPTY LINE")
					variabilities.pop();
			}
		}
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");
		varLine.add("NULL");

	}

}
