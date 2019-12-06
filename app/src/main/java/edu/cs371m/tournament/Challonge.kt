package edu.cs371m.tournament

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.tournament.api.ChallongeInfo
import kotlinx.android.synthetic.main.challonge_view.*

class Challonge : AppCompatActivity(){
    private lateinit var viewModel: ChallongeViewModel
    private lateinit var list : List<ChallongeInfo>
    private lateinit var currentList  : MutableList<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.challonge_view)

        viewModel = ViewModelProviders.of(this)[ChallongeViewModel::class.java]
        viewModel.chosenTournament(intent.getStringExtra("url_string"))
        viewModel.chosenapiKey(intent.getStringExtra("api_string"))
        Log.d("debug", "In challonge" + viewModel.returnURL())

        viewModel.observeChallongeInfo().observe(this, Observer{
            list = it
            if(list.size >= 0){
                currentList = mutableListOf()
                for(i in 0.until(list.size)){
                    currentList.add(list[i].data.name)
                }

                //adapter
                viewManager = LinearLayoutManager(this)
                viewAdapter = ElimActivityAdapter(currentList)

                recyclerView = findViewById<RecyclerView>(R.id.recyclerViewChallonge).apply {
                    layoutManager = viewManager
                    adapter = viewAdapter
                }

                add_but.setOnClickListener {
                    val text = edit_but.text
                    if(text.isEmpty()){
                        Toast.makeText(this, "Text Box is empty.", Toast.LENGTH_LONG).show()
                    }
                    else if(currentList.contains(text.toString())){
                        Toast.makeText(this, "This name has already been taken. Please select another.", Toast.LENGTH_LONG).show()
                    }
                    else{
                        currentList.add(text.toString())
                        edit_but.text.clear()
                        viewAdapter.notifyDataSetChanged()
                    }
                }
                del_but.setOnClickListener {
                    val text = edit_but.text
                    if(text.isEmpty()){
                        Toast.makeText(this, "Text Box is empty.", Toast.LENGTH_LONG).show()
                    }

                    if(currentList.contains(text.toString())){
                        currentList.remove(text.toString())
                        edit_but.text.clear()
                        viewAdapter.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(this, "This name is not currently in the list.", Toast.LENGTH_LONG).show()
                    }
                }
                single_button.setOnClickListener {
                    val intent = Intent(this, SingleElim::class.java)
                    intent.putExtra("list", ArrayList(currentList))
                    intent.putExtra("round", 1)
                    startActivity(intent)
                }
                double_button.setOnClickListener {
                    val intent = Intent(this, DoubleElim::class.java)
                    val loserList = ArrayList<String>()
                    intent.putExtra("winner_list", ArrayList(currentList))
                    intent.putExtra("loser_list", loserList)
                    intent.putExtra("round", 1)
                    startActivity(intent)
                }
                rr_button.setOnClickListener {
                    val intent = Intent(this, RoundRobin::class.java)
                    intent.putExtra("list", ArrayList(currentList))
                    intent.putExtra("round", 1)
                    startActivity(intent)
                }
                prelim_button.setOnClickListener {
                    val intent = Intent(this, PreLim::class.java)
                    intent.putExtra("list", ArrayList(currentList))
                    startActivity(intent)
                }
            }

        })
        viewModel.netRefreshChallonge()

        cancel_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }
}