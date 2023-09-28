package com.example.vahan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.ViewHolder> {

    private List<University> universityList;
    private Context context;

    public UniversityAdapter(List<University> universityList, Context context) {
        this.universityList = universityList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_university, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        University university = universityList.get(position);
        holder.textViewCountry.setText(university.getCountry());
        holder.textViewName.setText(university.getName());

        // Check if the university has web pages
        List<String> webPages = university.getWebpages();
        if (webPages != null && !webPages.isEmpty()) {
            // Set the first web page URL
            String firstWebPage = webPages.get(0);
            //holder.textViewWebpage.setText(firstWebPage);

            // Handle click on the webpage link
            holder.textViewWebpage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWebView(firstWebPage);
                }
            });
        } else {
            // No web pages available
            holder.textViewWebpage.setText("No website available");
        }
    }

    @Override
    public int getItemCount() {
        return universityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewWebpage, textViewCountry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewWebpage = itemView.findViewById(R.id.textViewWebsite);
            textViewCountry = itemView.findViewById(R.id.textViewCountry);
        }
    }

    private void openWebView(String webpageUrl) {
        // Create an intent to open the webpage in an in-app WebView
        Intent webViewIntent = new Intent(context, WebViewActivity.class);
        webViewIntent.putExtra("webpageUrl", webpageUrl);
        context.startActivity(webViewIntent);
    }
    public void updateData(List<University> newDataSet) {
        universityList.clear();
        universityList.addAll(newDataSet);
        notifyDataSetChanged();
    }






}
