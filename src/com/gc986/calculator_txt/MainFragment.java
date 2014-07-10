package com.gc986.calculator_txt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainFragment extends Fragment {

	String TAG = "calculator_txt";
	
	public MainFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
		
		Button bt_result = (Button) rootView.findViewById(R.id.button_result);
		bt_result.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText edit = (EditText) MainFragment.this.getView().findViewById(R.id.editText);
				Calculator calc = new Calculator(edit.getText().toString().replace(" ", ""));
				TextView tv_result = (TextView)  MainFragment.this.getView().findViewById(R.id.textView_result);
				tv_result.setText(String.valueOf(calc.get_result()));
			}
		});
		
		return rootView;
	}
	
	public class Calculator{
	
		private float result; // Результат вычисления
		
		// Выход
		LinkedList<String> exit = new LinkedList<String>(); 
		// Стек команд
		Stack<String> stack = new Stack<String>();
		
		/**Вычисление строки*/
		public Calculator (String txt_enter){
		
			List<String> elements = split_str(txt_enter);
			
			// Перебираем все символы
			for(int i = 0;i<elements.size();i++){
				if (check_oper(elements.get(i))==0){
					exit.add(elements.get(i)); // Добавили число
				} else {
					add_in_stack(elements.get(i)); // Добавляем в стек
				}
			}
			
			// Кидаем остатки из стека в выходную строку
			for (int i = stack.size()-1;i>=0;i--){
				exit.add(stack.pop());
			}

			// Вычисляем результат
			calc();
		}
		
		/**Вычисляем результат*/
		private void calc(){
			Stack<String> ls = new Stack<String>();
			// Перебирвем элементы
			for(int i = 0; i<exit.size();i++){
				if(check_oper(exit.get(i))==0) ls.push(exit.get(i)); // Добавляем в лист...
				else { // Вычисляем операцию
					if (exit.get(i).equals("+")){ // + Сложение
						float p = Float.parseFloat(ls.pop())+Float.parseFloat(ls.pop());
						ls.push(String.valueOf(p));
					}
					if (exit.get(i).equals("-")){ // - Вычитание
						float p1 = Float.parseFloat(ls.pop());
						float p2 = Float.parseFloat(ls.pop());
						float p = p2-p1;
						ls.push(String.valueOf(p));
					}
					if (exit.get(i).equals("*")){ // * Умножение
						float p = Float.parseFloat(ls.pop())*Float.parseFloat(ls.pop());
						ls.push(String.valueOf(p));
					}
					if (exit.get(i).equals("/")){ // / Деление
						float p1 = Float.parseFloat(ls.pop());
						float p2 = Float.parseFloat(ls.pop());
						float p = p2/p1;
						ls.push(String.valueOf(p));
					}
				}
			}
			result = Float.parseFloat(ls.pop());					
		}
		
		/**Добавляем в стек
		 * @return */
		private void add_in_stack(String str){
			if (stack.size()==0) { // стек был пуст
				stack.push(str);
			} else {
				if (check_oper(str)>0) // Работаем с операцией
					{
						for(int i = stack.size()-1;i>=0;i--){
							String el = stack.pop();
							if (check_oper(el)>=check_oper(str)) exit.add(el);
							else {
								stack.push(el);
								break;
							}
						}
						stack.push(str);
					}
				if (check_oper(str)<0) // Работаем со скобками
					{
						if (check_oper(str)==-1) stack.push(str); // (
						if (check_oper(str)==-2) // )
						{
							for(int i = stack.size()-1;i>=0;i--){
								String el = stack.pop();
								if (check_oper(el)>0) exit.add(el);
								if (check_oper(el)==-1) break; // (
							}
						}
					}
			}			
		}
		
		/**Получаем результат*/
		public float get_result(){
			return result;
		}
	
		/**определение что символ операция. Возвращает приоритет операции. Если 0 - это не операция. -1 - открывающая скобка, -2 - закрывающая скобка*/
		private int check_oper(String str){
			switch (str){
			case "+": return 1;
			case "-": return 1;
			case "*": return 2;
			case "/": return 2;
			case "(": return -1;
			case ")": return -2;
			}
			return 0;
		}
	
		/**Разбиваем строку на части*/
		private List<String> split_str(String str){
			List<String> lines = new ArrayList<String>();
			String number = "";
			for(int i = 0;i<str.length();i++){
				// Проверка что это операция
				if(check_oper(String.valueOf(str.charAt(i)))!=0){
					boolean check = true;
					if (i>0) { // Отрицательное число
						if ((check_oper(String.valueOf(str.charAt(i-1)))>0)&&(String.valueOf(str.charAt(i)).equals("-"))){ 
							if (number.length()>0) lines.add(number);
							number = "-";
							check = false;
						}
					} 
					
					if (check) {
						if (number.length()>0) lines.add(number);
						number = "";
						lines.add(String.valueOf(str.charAt(i)));
					}
				}
				// Проверк что это скобка
				if(check_oper(String.valueOf(str.charAt(i)))==0){
					number = number + String.valueOf(str.charAt(i));
				}
			}
			
			if (number.length()>0) lines.add(number);
			
			return lines;
		}
	}
}
