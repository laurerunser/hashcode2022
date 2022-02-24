import java.util.ArrayList;
import java.util.Comparator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static int nb_contrib;
    static int nb_projects;
    static int max_time; // biggest best before * 2

    static ArrayList<Project> projects = new ArrayList<>();
    static ArrayList<Contributor> contributors = new ArrayList<>();

    static String[] filenames = {"a_an_example.in.txt",
            "b_better_start_small.in.txt", "c_collaboration.in.txt",
            "d_dense_schedule.in.txt", "e_exceptional_skills.in.txt",
            "f_find_great_mentors.in.txt"};

    public static void main(String[] args) throws FileNotFoundException {
        InputStream ins = new FileInputStream("src/input_data/" + filenames[0]);
        Scanner obj = new Scanner(ins);
        parse(obj);
        max_time=1000;
        traitement(max_time);
        /*for(int i=0;i<projects.size();i++){
            System.out.println(projects.get(i).completed);
        }*/
        try{
            write_sol("test.txt",projects);
        }catch(Exception E){
            E.printStackTrace();
        }


    }


    // parser
    public static void parse(Scanner sc) {
        String[] l = sc.nextLine().split(" ");
        nb_contrib = Integer.parseInt(l[0]);
        nb_projects = Integer.parseInt(l[1]);

        // parse contributors
        for (int i = 0; i<nb_contrib; i++) {
            Contributor c = new Contributor();
            contributors.add(c);

            String[] l1 = sc.nextLine().split(" ");
            c.name = l1[0];
            c.nb_skill = Integer.parseInt(l1[1]);
            c.skills = new HashMap<>();
            for (int j = 0; j<c.nb_skill; j++) {
                String[] l2 = sc.nextLine().split(" ");
                c.skills.put(l2[0], Integer.parseInt(l2[1]));
            }
        }

        // parse projects
        for (int i = 0; i < nb_projects; i++) {
            Project p = new Project();
            projects.add(p);

            String[] l1 = sc.nextLine().split(" ");
            p.name = l1[0];
            p.time = Integer.parseInt(l1[1]);
            p.score = Integer.parseInt(l1[2]);
            p.best_before = Integer.parseInt(l1[3]);
            p.nb_contrib = Integer.parseInt(l1[4]);
            p.roles = new LinkedList<>();
            for (int j = 0; j<p.nb_contrib; j++) {
                Role r = new Role();
                p.roles.add(r);

                String[] l2 = sc.nextLine().split(" ");
                r.name = l2[0];
                r.skill = Integer.parseInt(l2[1]);
            }
        }
    }

    // algo

    static void sort_projects() {
        projects.sort((o1, o2) -> {
            if (o1.score == o2.score) {
                return 0;
            } else if (o1.score > o2.score) {
                return 1;
            } else {
                return -1;
            }
        });
    }
    static ArrayList<Contributor> get_available_contributors(int current_time) {
        ArrayList<Contributor> available = new ArrayList<>();
        for (Contributor c : contributors) {
            if (c.available >= current_time) {
                available.add(c);
            }
        }
        return available;
    }

    static void traitement(int max_time){
        sort_projects();

        for(int current_time=0;current_time<max_time;current_time++){
            for(int i=0;i<projects.size();i++){
                Project p=projects.get(i);
                LinkedList<Role> ptmp=p.roles;
                Role last=ptmp.getLast();
                int j=0;
                Role rtmp;
                ArrayList<Contributor> available=get_available_contributors(current_time);
                boolean f=true;
                LinkedList<Contributor> used=new LinkedList<Contributor>();
                do{
                    rtmp=ptmp.get(j);
                    String c=skill_available(available,rtmp.name,rtmp.skill,used);
                    if(c.equals("1")){
                        f=false;
                    }
                    else{
                        rtmp.contributor=c;
                    }
                }while(rtmp!=last && f);
                if(f){
                    for(int k=0;k<used.size();k++){
                        used.get(k).available=current_time+p.time;
                    }
                }
                p.completed=true;
            }
        }
    
    }
    public static String skill_available(ArrayList<Contributor> a, String s, int lvl,LinkedList<Contributor> used){
        for (int i=0;i<a.size();i++){
            Contributor tmp=a.get(i);
            if(tmp.skills.containsKey(s)){
                int clvl=tmp.skills.get(s);
                if(clvl>=lvl){
                    a.remove(tmp);
                    used.add(tmp);
                    return tmp.name;
                }
            }
        }
        return "1";
    }

    static public void write_sol(String filePath, ArrayList<Project> ps)throws IOException{
        PrintWriter writer = new PrintWriter(filePath, StandardCharsets.US_ASCII);
        LinkedList<Project> completed=new LinkedList<Project>();
        int i;
        for(i=0;i<ps.size();i++){
            if(ps.get(i).completed){
                completed.add(ps.get(i));
            }
        }
        System.out.println(Integer.toString(i));
        writer.println(Integer.toString(i));
        for(int j=0;j<i;j++){
            LinkedList<Role> tmp=completed.get(j).roles;
            writer.println(completed.get(j).name);
            String ret=tmp.get(0).contributor;
            
            for(int k=1;k<tmp.size();k++){
                ret+=" "+tmp.get(k).contributor;
            }
            writer.println(ret);
        }
        writer.close();
    }

   
    // write sol
}
