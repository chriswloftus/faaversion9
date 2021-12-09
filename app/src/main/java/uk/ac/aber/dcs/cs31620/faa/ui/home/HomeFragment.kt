/**
 * Represents the home page. Currently shows some
 * static text an ImageView and a randomly selected
 * featured cat. Data is hard coded.
 * @author Chris Loftus
 * @version 2
 */
package uk.ac.aber.dcs.cs31620.faa.ui.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import uk.ac.aber.dcs.cs31620.faa.databinding.FragmentHomeBinding
import uk.ac.aber.dcs.cs31620.faa.model.RecentCatsViewModel
import kotlin.random.Random

class HomeFragment : Fragment() {

    private lateinit var  homeFragmentBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)

        val featuredCatImg = homeFragmentBinding.featuredImage
        val catViewModel: RecentCatsViewModel by viewModels()
        val recentCats = catViewModel.recentCats

        recentCats.observe(viewLifecycleOwner){
            if (it.isNotEmpty()) {
                Log.i("FAA", it.size.toString())
                if (it.isNotEmpty()) {
                    val catPos = Random.nextInt(it.size)
                    val catImage = it[catPos].imagePath

                    if (catImage.isNotEmpty()) {
                        Glide.with(this)
                            .load(Uri.parse(catImage))
                            .into(featuredCatImg)
                    }
                }
            }
        }

        return homeFragmentBinding.root
    }

}